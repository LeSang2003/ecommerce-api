package com.demo;

import com.demo.model.*;
import com.demo.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ColorRepository colorRepository,
            SizeRepository sizeRepository,
            ProductRepository productRepository
    ) {

        return args -> {

            // =========================
            // ADMIN
            // =========================
            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();

                admin.setUsername("admin");

                admin.setPassword(
                        new BCryptPasswordEncoder()
                                .encode("123456")
                );

                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }

            // =========================
            // CATEGORY
            // =========================
            Category tshirt;
            Category pants;

            if (categoryRepository.count() == 0) {

                tshirt = categoryRepository.save(
                        new Category("T-Shirt")
                );

                pants = categoryRepository.save(
                        new Category("Pants")
                );

            } else {

                tshirt = categoryRepository.findAll()
                        .stream()
                        .filter(c -> c.getName().equals("T-Shirt"))
                        .findFirst()
                        .orElse(null);

                pants = categoryRepository.findAll()
                        .stream()
                        .filter(c -> c.getName().equals("Pants"))
                        .findFirst()
                        .orElse(null);
            }

            // =========================
            // COLORS
            // =========================
            Color red;
            Color black;
            Color white;
            Color brown;
            Color gray;

            if (colorRepository.count() == 0) {

                red = colorRepository.save(
                        new Color("Red")
                );

                black = colorRepository.save(
                        new Color("Black")
                );

                white = colorRepository.save(
                        new Color("White")
                );

                brown = colorRepository.save(
                        new Color("Brown")
                );

                gray = colorRepository.save(
                        new Color("Gray")
                );

            } else {

                red = colorRepository.findAll()
                        .stream()
                        .filter(c -> c.getName().equals("Red"))
                        .findFirst()
                        .orElse(null);

                black = colorRepository.findAll()
                        .stream()
                        .filter(c -> c.getName().equals("Black"))
                        .findFirst()
                        .orElse(null);

                white = colorRepository.findAll()
                        .stream()
                        .filter(c -> c.getName().equals("White"))
                        .findFirst()
                        .orElse(null);

                brown = colorRepository.findAll()
                        .stream()
                        .filter(c -> c.getName().equals("Brown"))
                        .findFirst()
                        .orElse(null);

                gray = colorRepository.findAll()
                        .stream()
                        .filter(c -> c.getName().equals("Gray"))
                        .findFirst()
                        .orElse(null);
            }

            // =========================
            // SIZES
            // =========================
            Size s;
            Size m;
            Size l;
            Size xl;

            if (sizeRepository.count() == 0) {

                s = sizeRepository.save(
                        new Size("S")
                );

                m = sizeRepository.save(
                        new Size("M")
                );

                l = sizeRepository.save(
                        new Size("L")
                );

                xl = sizeRepository.save(
                        new Size("XL")
                );

            } else {

                s = sizeRepository.findAll()
                        .stream()
                        .filter(size -> size.getName().equals("S"))
                        .findFirst()
                        .orElse(null);

                m = sizeRepository.findAll()
                        .stream()
                        .filter(size -> size.getName().equals("M"))
                        .findFirst()
                        .orElse(null);

                l = sizeRepository.findAll()
                        .stream()
                        .filter(size -> size.getName().equals("L"))
                        .findFirst()
                        .orElse(null);

                xl = sizeRepository.findAll()
                        .stream()
                        .filter(size -> size.getName().equals("XL"))
                        .findFirst()
                        .orElse(null);
            }

            // =========================
            // PRODUCT
            // =========================
            if (productRepository.count() == 0) {

                Product p = new Product();

                p.setName("Basic T-Shirt");

                p.setDescription("Cotton 100%");

                p.setPrice(199000.0);

                p.setStock(100);

                p.setImageUrl("tshirt.jpg");

                p.setCategory(tshirt);

                p.setColors(
                        Set.of(
                                red,
                                black,
                                white,
                                brown,
                                gray
                        )
                );

                p.setSizes(
                        Set.of(
                                s,
                                m,
                                l,
                                xl
                        )
                );

                productRepository.save(p);
            }
        };
    }
}