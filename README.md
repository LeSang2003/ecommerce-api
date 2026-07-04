# HYNO Fashion — E-commerce Backend API

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

A production-deployed REST API powering **HYNO Fashion**, a full-featured fashion e-commerce platform. Built solo from scratch with **Spring Boot 3** and **Java 21**, it covers everything a real online store needs — authentication, a full product catalog with variants, cart and checkout, two independent payment gateways, transactional email, coupons, reviews, wishlists, and a complete admin back-office with analytics and reporting.

This is not a tutorial clone — the codebase reflects decisions made while shipping to real infrastructure (Railway, Vercel, Neon) and debugging the kind of issues that only show up in production, several of which are documented below.

🔗 **Live API:** `https://endearing-empathy-production-db34.up.railway.app`
🔗 **Frontend (separate repo):** [ecommerce-dashboard](https://github.com/LeSang2003/ecommerce-dashboard) (React + Vite)
🔗 **Live Demo:** `https://ecommerce-dashboard-liart-theta.vercel.app`

---

## 📚 Table of Contents

- [Tech Stack](#️-tech-stack)
- [Demo Credentials](#-demo-credentials)
- [Key Features](#-key-features)
- [Architecture & Notable Technical Decisions](#️-architecture--notable-technical-decisions)
- [Payment Flows Explained](#-payment-flows-explained)
- [Security & Authentication](#-security--authentication)
- [API Overview](#-api-overview)
- [Database Schema](#️-database-schema-core-entities)
- [Error Handling](#-error-handling)
- [Project Structure](#-project-structure)
- [Environment Configuration](#️-environment-configuration)
- [Running Locally](#-running-locally)
- [Known Limitations / Roadmap](#-known-limitations--roadmap)

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language / Runtime | Java 21 |
| Framework | Spring Boot 3.5 (Web, Data JPA, Security, Validation) |
| Authentication | JWT (`jjwt`), stateless sessions |
| Password Hashing | BCrypt |
| Database | PostgreSQL (hosted on Neon), Hibernate ORM |
| Payment Gateways | VNPay (sandbox, HMAC-SHA512 signed), Stripe Checkout |
| Transactional Email | SendGrid Web API |
| Image Hosting | Cloudinary |
| Reporting | Apache POI (Excel `.xlsx` export), iText (PDF export) |
| Build Tool | Maven |
| Hosting | Railway (containerized deploy from GitHub) |

---
## 🔑 Demo Credentials

An admin account is seeded automatically on first application startup via [`DataInitializer`](src/main/java/com/demo/DataInitializer.java), along with a starter category, colors, sizes, and one sample product — so the API and admin dashboard are usable immediately after deploy, with no manual database setup.

**Demo credentials available on request** — feel free to reach out via [le657930@gmail.com] and I'll be happy to share admin access for a walkthrough.

Alternatively, register a brand-new customer account through the app itself to try the full purchase flow end-to-end: browse → add to cart → checkout → pay via VNPay sandbox or Stripe test mode → receive a confirmation email → track the order status.

Feel free to register a brand-new customer account through the app itself to try the full purchase flow end-to-end: browse → add to cart → checkout → pay via VNPay sandbox or Stripe test mode → receive a confirmation email → track the order status.

---

## ✨ Key Features

### Customer-facing

- **Accounts** — registration with mandatory email verification before login is allowed, forgot/reset password via emailed one-time tokens, profile editing, avatar upload, password change.
- **Catalog** — products with multiple images, price, stock, material, and gender attributes; many-to-many color and size variants; category and seasonal collection grouping; related-products suggestions; server-side pagination, keyword search, and multi-field filtering/sorting for the storefront.
- **Cart & Checkout** — persistent server-side cart tied to the authenticated user; coupon code validation with minimum order value, usage caps, and expiry; two independent checkout paths (cart-based `checkout()` and a direct `createOrder()` used by the guarded VNPay/Stripe flow).
- **Payments** — VNPay (HMAC-SHA512 signed redirect flow, Vietnamese domestic payment) and Stripe Checkout (international cards), both wired to the same order-confirmation and email logic.
- **Orders** — full lifecycle tracking (`Pending → Confirmed → Shipping → Completed`, or `Cancelled`), self-service cancellation for still-pending orders with automatic stock restoration, paginated order history.
- **Reviews** — star ratings + comments + optional photo, restricted to users who actually purchased the product in a completed order, one review per product per user.
- **Wishlist** — toggle-based add/remove, with admin-facing aggregate stats (most-wished products, unique users engaged).
- **Newsletter & Contact** — public subscribe/contact-form endpoints, both triggering automated confirmation emails.
- **Lookbooks** — editorial-style fashion campaign pages built from ordered sections (image / text / quote / video), each with its own images and layout type — designed for a magazine-like storytelling experience rather than a flat gallery.

### Admin

- **Dashboard** — total users/orders/products, total & time-boxed revenue (today / last 7 days / last 30 days / by month), order-status breakdown, top-selling products, top-spending customers, low-stock alerts.
- **Catalog management** — full CRUD for products (with bulk image upload), categories, collections (with banner upload and performance stats), colors, and sizes.
- **Order management** — paginated + filterable order list (by status and/or payment method), order detail view, manual status updates (which trigger a status-change email to the customer), Excel/PDF export.
- **User management** — role promotion/demotion, ban/unban (self-protection built in — an admin cannot ban themselves).
- **Coupon management** — create/update/delete/toggle, with live usage stats (total, active, used, most-used coupon).
- **Newsletter & Contact inbox** — list, mark-as-read, reply-by-email (which also updates the message record), stats, Excel/PDF export.

---

## 🏗️ Architecture & Notable Technical Decisions

A few real production issues encountered while deploying this project, and how they were diagnosed and fixed — included here deliberately, because working through problems like these is a bigger part of the job than writing the happy path:

- **SMTP silently blocked on Railway.** Railway (like many PaaS providers) blocks outbound ports 587/465 to curb spam abuse, which made `JavaMailSender`-based Gmail SMTP delivery fail with no useful error in the app logs. Diagnosed by testing the same SMTP config locally (worked) vs. on Railway (didn't), then migrated the entire email layer to the **SendGrid Web API over HTTPS (port 443)**, which is never blocked. The `MailService` public interface was kept identical, so no calling code in `OrderService`, `UserService`, `ContactService`, etc. had to change.
- **Invalid `Location` header on payment redirect.** After switching the post-payment redirect target from a hardcoded `localhost` URL to an environment variable, requests started failing with `Invalid characters (CR/LF) in header Location`. Root cause: a stray newline character picked up when the environment variable was set via the hosting dashboard. Fixed at the code level with defensive `.trim()` on the value (so the fix holds regardless of how the variable is set in the future), in addition to cleaning up the variable itself.
- **"Paid" is not "fulfilled."** Early versions were tempted to mark an order `COMPLETED` the moment payment succeeded. That's wrong for a physical goods store — a paid order still has to ship. The final design only auto-transitions to `CONFIRMED` on successful payment; `SHIPPING` and `COMPLETED` are always explicit admin actions, keeping the payment-confirmation logic and the fulfillment-tracking logic cleanly separated.
- **Stock consistency across cancellation.** Stock is decremented at order-creation time (so two customers can't both "buy" the last unit while one is still filling out the payment form), and is restored automatically if the customer cancels a still-`PENDING` order — but cancellation is deliberately blocked once an order has moved past `PENDING`, since stock may already be committed to fulfillment.
- **Preventing fake reviews.** A review submission is rejected unless the requesting user is the owner of the referenced order, that order's status is `COMPLETED`, and the order actually contains the product being reviewed — plus a uniqueness check so the same user can't review the same product twice.

---

## 💳 Payment Flows Explained

### VNPay (sandbox)

1. Frontend calls `GET /api/payment/vnpay?orderId=&amount=`.
2. `VNPayService` builds the full VNPay parameter set, sorts and URL-encodes it, signs it with **HMAC-SHA512** using the merchant hash secret, and returns a ready-to-redirect payment URL.
3. The customer completes payment on VNPay's sandbox page.
4. VNPay redirects the browser back to `GET /api/payment/vnpay-return` with signed query parameters (`vnp_ResponseCode`, `vnp_TxnRef`, `vnp_TransactionNo`, `vnp_BankCode`).
5. On response code `"00"` (success), the order is updated with the transaction number, bank code, and payment timestamp, its status moves to `CONFIRMED`, a **payment-success email** is sent via SendGrid, and the browser is redirected to the frontend's `/payment-success` page. Any other response code redirects to `/payment-failed`.

### Stripe Checkout

1. Frontend calls `POST /api/payment/checkout` with an amount and order ID.
2. `PaymentService` creates a Stripe Checkout `Session` in payment mode, with the order ID stashed in Stripe's `metadata` so it can be recovered on the success callback, and returns the hosted Stripe payment page URL.
3. After payment, Stripe redirects to `GET /api/payment/success?session_id=`, which retrieves the session, reads back the `orderId` from metadata, confirms the order (`CONFIRMED`), and sends the same payment-success email flow as the VNPay path.

Both flows converge on the same `MailService.sendPaymentSuccessEmail(...)` call, so the customer gets a consistent confirmation email regardless of which gateway they used.

---

## 🔐 Security & Authentication

- Stateless authentication using **JWT** (`jjwt`), issued on login (`JwtService.generateToken`, 24h expiry, subject = username, custom claims for `id` and `role`) and validated on every request by a custom `JwtAuthenticationFilter` (`OncePerRequestFilter`) that sits before Spring Security's standard username/password filter.
- Passwords are hashed with **BCrypt** (`BCryptPasswordEncoder`) — never stored or compared in plaintext.
- **Role-based access control** with two roles, `USER` and `ADMIN`, enforced at two levels:
  - Spring Security's filter chain (`SecurityConfig`) — `/api/admin/**` and `/api/orders/admin/**` require `ROLE_ADMIN`; `/api/orders/**`, `/api/cart/**`, `/api/reviews/**`, `/api/users/**` require any authenticated user; `/api/auth/**`, `/api/products/**`, `/api/newsletter/**` are public.
  - Inside individual controllers/services for ownership checks (e.g. a user can only cancel/review *their own* orders; an admin cannot ban/unban themselves).
- **Email verification gate** — a newly registered account has `enabled = false` until the emailed verification link is clicked; `UserDetails.isEnabled()` also independently checks the `banned` flag, so a banned-but-verified user is still rejected at authentication time.
- **Ban enforcement at the filter level** — `JwtAuthenticationFilter` explicitly checks `user.getBanned()` (except for admins) and returns `403 Forbidden` before the request ever reaches a controller, rather than relying solely on `UserDetails.isEnabled()`.
- Time-boxed, single-use tokens for both email verification and password reset, each stored in their own table with an `expiryDate`, and deleted once consumed.

---

## 📡 API Overview

<details>
<summary><strong>Auth — <code>/api/auth</code></strong></summary>

| Method | Endpoint | Description |
|---|---|---|
| POST | `/register` | Register a new account (sends verification email) |
| POST | `/login` | Authenticate and receive a JWT |
| GET | `/verify?token=` | Verify email address |
| POST | `/forgot-password` | Request a password-reset email |
| POST | `/reset-password` | Reset password using a token |

**Example — `POST /api/auth/register`**
```json
// Request
{ "username": "james", "email": "james@example.com", "password": "secret123" }

// Response 200
"Register success. Please verify your email."
```

**Example — `POST /api/auth/login`**
```json
// Request
{ "username": "james", "password": "secret123" }

// Response 200 (raw JWT string)
"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW1lcyIs..."
```

</details>

<details>
<summary><strong>Users — <code>/api/users</code></strong></summary>

| Method | Endpoint | Description |
|---|---|---|
| GET | `/me` | Get current user profile |
| PUT | `/profile` | Update full name, phone, address, birthday, gender |
| PUT | `/avatar` | Update avatar URL (uploaded to Cloudinary beforehand) |
| PUT | `/change-password` | Change password (requires old password + confirmation) |

</details>

<details>
<summary><strong>Products, Categories, Collections, Colors, Sizes</strong></summary>

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/products` | List all products (annotated with sold count & average rating) |
| GET | `/api/products/{id}` | Product detail |
| GET | `/api/products/{id}/related` | Up to 4 related products from the same category |
| GET | `/api/products/collection/{slug}` | Products belonging to a collection |
| GET | `/api/categories-by-gender?gender=` | Categories that have products for a given gender |
| POST/PUT/DELETE | `/api/admin/products/**` | Admin product CRUD |
| GET | `/api/admin/products/search` | Paginated search with keyword, category, collection, price range, and sort |
| GET | `/api/admin/products/low-stock` | Products with stock below 10 units |
| POST | `/api/admin/products/{id}/images` | Bulk multi-image upload for a product |
| CRUD | `/api/categories`, `/api/collections`, `/api/colors`, `/api/sizes` | Catalog reference-data management |
| GET | `/api/collections/featured` `/stats` `/top-performance` | Collection homepage feature + analytics |

</details>

<details>
<summary><strong>Cart — <code>/api/cart</code></strong></summary>

| Method | Endpoint | Description |
|---|---|---|
| POST | `/add` | Add a product to the current user's cart |
| GET | `/` | View raw cart items |
| PUT | `/{id}?quantity=` | Update quantity of a cart line |
| DELETE | `/{id}` | Remove a cart line |
| DELETE | `/clear` | Empty the cart |
| GET | `/total` | Total cart value |
| GET | `/summary` | Cart items + subtotals + grand total, DTO-mapped |

</details>

<details>
<summary><strong>Orders — <code>/api/orders</code></strong></summary>

| Method | Endpoint | Description |
|---|---|---|
| POST | `/checkout` | Convert the current cart into a `PENDING` order |
| POST | `/` | Create an order directly from a request body (used by the VNPay/Stripe flow; supports coupon, color/size, customer info) |
| GET | `/my-orders` | Paginated order history for the current user |
| GET | `/{id}` | Order detail with items |
| GET | `/{orderId}/items` | Raw order-item list |
| PUT | `/{id}/cancel` | Cancel a `PENDING` order owned by the current user (restores stock) |
| PUT | `/{id}/status?status=` | **Admin:** update order status (sends a status-change email) |
| GET | `/admin/all` `/admin/orders` `/admin/orders/filter` | Admin order listing, plain / paginated / filtered by status+payment method |
| GET | `/admin/orders/{id}` | Admin order detail DTO |
| GET | `/admin/dashboard` | Aggregated dashboard stats |
| GET | `/admin/revenue` `/revenue/today` `/revenue-7-days` `/revenue-30-days` `/revenue-by-month` | Revenue analytics at different granularities |
| GET | `/admin/orders/today` `/orders/latest` `/orders/statistics` | Order-count and status-breakdown analytics |
| GET | `/admin/top-products` `/customers/top` | Top-seller and top-spender leaderboards |
| GET | `/admin/orders/export/excel` `/export/pdf` | Export the full order list |
| PUT | `/admin/users/{id}/role?role=` | Change a user's role |
| PUT | `/admin/users/{id}/toggle-ban` | Ban/unban a user |
| GET | `/admin/users` | Paginated user list |

</details>

<details>
<summary><strong>Payment — <code>/api/payment</code></strong></summary>

| Method | Endpoint | Description |
|---|---|---|
| POST | `/checkout?orderId=&amount=` | Create a Stripe Checkout session, returns the hosted payment URL |
| GET | `/success?session_id=` | Stripe success callback — confirms order, sends email |
| GET | `/vnpay?orderId=&amount=` | Generate a signed VNPay payment URL |
| GET | `/vnpay-return` | VNPay return callback — verifies response code, confirms order, sends email, redirects to frontend |

</details>

<details>
<summary><strong>Coupons, Reviews, Wishlist, Contact, Newsletter, Lookbooks</strong></summary>

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/coupons/validate?code=&totalPrice=` | Validate a coupon against active/expired/min-order/usage-cap rules |
| GET | `/api/coupons/stats` | Coupon usage stats |
| CRUD + `/toggle` | `/api/admin/coupons` | Admin coupon management |
| POST/GET/PUT/DELETE | `/api/reviews` | Create/list/update/delete purchase-verified reviews |
| GET | `/api/reviews/product/{id}` `/{id}/average` | Reviews and average rating for a product |
| POST | `/api/wishlist/{productId}` | Toggle wishlist membership |
| GET | `/api/wishlist` `/check/{productId}` | List / check current user's wishlist |
| DELETE | `/api/wishlist/{productId}` | Remove from wishlist |
| GET | `/api/wishlist/stats` `/top-products` | Wishlist analytics |
| POST | `/api/contact` | Submit the contact form (notifies admin + confirms to sender) |
| GET/DELETE/PUT | `/api/contact` | Admin inbox: list / delete / mark-as-read |
| POST | `/api/contact/reply` | Reply to a message by email |
| GET | `/api/contact/stats` `/export/excel` `/export/pdf` | Contact analytics & export |
| POST | `/api/newsletter/subscribe` | Subscribe (or reactivate) to the newsletter |
| GET | `/api/newsletter` `/stats` `/export/excel` `/export/pdf` | Admin subscriber management |
| CRUD | `/api/lookbooks` | Lookbook management, including nested sections & images |
| GET | `/api/lookbooks/featured` `/{slug}` `/stats` | Public lookbook browsing & stats |

</details>

---

## 🗄️ Database Schema (Core Entities)

```
User (username, email, password, role, enabled, banned, profile fields)
 │
 ├──< Order (status, totalPrice, paymentMethod, transactionNo, bankCode,
 │           paymentTime, couponCode, discountPercent, customerName/phone/address)
 │      └──< OrderItem (quantity, price, color, size) >── Product
 │
 ├──< CartItem >── Product
 ├──< Wishlist >── Product
 ├──< Review (rating 1-5, comment, imageUrl) >── Product, Order
 ├── EmailVerificationToken (1:1, token + expiryDate)
 └── PasswordResetToken (1:1, token + expiryDate)

Product (name, description, price, stock, material, gender, imageUrl)
 ├──< ProductImage (gallery)
 ├──> Category (N:1)
 ├──> Collection (N:1, nullable)
 ├──< >── Color   (M:N via product_colors)
 └──< >── Size    (M:N via product_sizes)

DiscountCoupon (code, discountPercent, minOrderValue, maxUsage, usedCount, active, expiredAt)

Lookbook (title, slug, season, year, coverImage, featured)
 ├──< LookbookSection (type: IMAGE/QUOTE/TEXT/VIDEO, title, content, videoUrl)
 │      └──< LookbookImage (imageUrl, displayOrder, layoutType)
 └──< LookbookImage (top-level images, outside any section)

ContactMessage (name, email, subject, message, readStatus, replied, replyContent, repliedAt)
NewsletterSubscriber (email — unique, active, subscribedAt)
```

**Enums**
- `OrderStatus`: `PENDING`, `CONFIRMED`, `SHIPPING`, `COMPLETED`, `CANCELLED`
- `Role`: `USER`, `ADMIN`
- `SectionType` (Lookbook): `IMAGE`, `QUOTE`, `TEXT`, `VIDEO`
- `LayoutType` (Lookbook image): `FULL`, `HALF`, `TRIPLE`, `GRID`

`Order` deliberately stores a **snapshot** of `couponCode`/`discountPercent` at purchase time rather than a live foreign key to `DiscountCoupon` — so historical orders keep showing the discount that was actually applied even if the coupon is later edited, deactivated, or deleted.

---

## ⚠️ Error Handling

A `@RestControllerAdvice`-based `GlobalExceptionHandler` centralizes error responses instead of leaking stack traces or inconsistent shapes:

```json
// Any RuntimeException (e.g. "Cart is empty", "Coupon expired", "Product not found")
{ "timestamp": "...", "status": 400, "error": "Cart is empty" }

// Any unhandled Exception
{ "timestamp": "...", "status": 500, "error": "Internal Server Error" }
```

This keeps business-rule violations (out-of-stock, invalid coupon, unauthorized cancellation, etc.) mapped to a predictable `400` shape the frontend can render directly, while genuinely unexpected failures are hidden behind a generic `500`.

---

## 📁 Project Structure

```
src/main/java/com/demo/
├── controller/     # REST controllers, one per resource
├── service/        # Business logic (+ service/impl for interface-based services)
├── repository/     # Spring Data JPA repositories, incl. native/JPQL analytics queries
├── model/          # JPA entities & enums
├── dto/            # Request/response DTOs (incl. dto/lookbook for nested payloads)
├── config/         # Security, VNPay, Stripe, Cloudinary, CORS, static resource config
├── security/       # JWT filter/service, CustomUserDetailsService
├── exception/      # Global exception handling, NotFoundException
├── spec/           # JPA Specifications (dynamic product search/filter)
└── DataInitializer # Seeds an admin account + starter catalog on first boot
```

---

## ⚙️ Environment Configuration

All sensitive configuration is read from environment variables — nothing is committed to source control:

```env
# Database
DATABASE_URL=
DATABASE_USERNAME=
DATABASE_PASSWORD=

# SendGrid
SENDGRID_API_KEY=
SENDGRID_FROM_EMAIL=
SENDGRID_FROM_NAME=

# Frontend URL (used for post-payment redirects & links inside emails)
FRONTEND_URL=

# Stripe
STRIPE_SECRET_KEY=

# VNPay (sandbox)
VNPAY_TMN_CODE=
VNPAY_HASH_SECRET=

# Cloudinary
CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=
```

---

## 🚀 Running Locally

**Prerequisites:** JDK 21, Maven, a PostgreSQL database (local or a free Neon instance).

```bash
git clone https://github.com/LeSang2003/ecommerce-api.git
cd ecommerce-api

# Provide the environment variables listed above,
# either via application.yml or your shell/IDE run configuration

mvn clean install
mvn spring-boot:run
```

The server starts on `http://localhost:8085` by default. On first run, `DataInitializer` seeds the `admin/123456` account plus a starter category, color/size set, and sample product, so the API is immediately testable with Postman/curl or the paired frontend without any manual SQL.

---

## 📌 Known Limitations / Roadmap

Being upfront about what's *not* done yet:

- VNPay and Stripe are wired to **sandbox/test credentials** — no real transactions occur.
- CORS is currently configured permissively (`allowedOriginPatterns: *`) for demo convenience; a production deployment would lock this down to the exact frontend origin.
- JWT is a single 24-hour access token with no refresh-token rotation yet.
- No automated test suite yet — this is the next thing I'd add given more time (starting with `OrderService` and `VNPayService`'s signature generation, since those carry the most business risk).
- File uploads have two code paths (local disk under `/uploads` for lookbook/category images vs. Cloudinary for products) — consolidating everything onto Cloudinary is a planned cleanup.

This project was built solo for learning/portfolio purposes, to demonstrate the ability to design and ship a complete backend e-commerce system end-to-end: relational database design, authentication & authorization, two independently-integrated payment gateways, transactional email at scale, and debugging real infrastructure issues that only surface once code leaves `localhost`.

---

## 👤 Author

**Le Sang** — [GitHub](https://github.com/LeSang2003)
