package com.beyond.ordersystem.product.service;

import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.dto.ProductCreateReqDto;
import com.beyond.ordersystem.product.dto.ProductResDto;
import com.beyond.ordersystem.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResDto productCreate(ProductCreateReqDto dto) {
        MultipartFile image= dto.getProductImage();
        Product product = dto.toEntity();
        Product savedProduct = productRepository.save(product);
        try {
            byte[] bytes = image.getBytes();
            Path path = Paths.get("/Users/qkdrmawll/Documents/ordersystem/",
                    savedProduct.getId()+"_"+ image.getOriginalFilename());
            Files.write(path,bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            savedProduct.updateImagePath(path.toString());

            return new ProductResDto().fromEntity(savedProduct);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        }
    }

    public ProductResDto productAwsCreate(ProductCreateReqDto dto) {
        MultipartFile image= dto.getProductImage();
        Product product = dto.toEntity();
        Product savedProduct = productRepository.save(product);
        try {
            byte[] bytes = image.getBytes();
            Path path = Paths.get("/Users/qkdrmawll/Documents/ordersystem/",
                    savedProduct.getId()+"_"+ image.getOriginalFilename());
            Files.write(path,bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            savedProduct.updateImagePath(path.toString());

            return new ProductResDto().fromEntity(savedProduct);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패");
        }
    }

    public Page<ProductResDto> productList(Pageable pageable) {
        Page<Product> products = productRepository.findByDelYn(pageable, "N");
        return products.map(a->new ProductResDto().fromEntity(a));
    }
}
