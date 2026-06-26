package com.demo.controller;

import com.demo.model.Size;
import com.demo.service.SizeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sizes")
public class SizeController {

    private final SizeService sizeService;

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    @GetMapping
    public List<Size> getAll() {
        return sizeService.getAllSizes();
    }

    @PostMapping
    public Size create(@RequestBody Size size) {
        return sizeService.save(size);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sizeService.deleteSize(id);
    }
}