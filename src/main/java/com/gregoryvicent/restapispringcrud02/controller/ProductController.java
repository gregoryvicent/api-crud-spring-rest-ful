package com.gregoryvicent.restapispringcrud02.controller;

import com.gregoryvicent.restapispringcrud02.exeptions.ProductNotFoundExeption;
import com.gregoryvicent.restapispringcrud02.model.Product;
import com.gregoryvicent.restapispringcrud02.reponsitory.ProductRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(path = "/api")
public class ProductController {

    private final ProductRepository productRepository;
    private final ProductModelAssembler assembler;

    ProductController(ProductRepository productRepository, ProductModelAssembler assembler) {
        this.productRepository = productRepository;
        this.assembler = assembler;
    }

    @GetMapping(path = "/products")
    public CollectionModel<EntityModel<Product>> getAllProducts() {
        List<EntityModel<Product>> products = productRepository.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(products, linkTo(methodOn(ProductController.class).getAllProducts()).withSelfRel());
    }

    @GetMapping(path = "/products/{id}")
    public EntityModel<Product> getOneProduct(@PathVariable Long id) {
        Product product = productRepository
                .findById(id)
                .orElseThrow(() -> new ProductNotFoundExeption(id));

        return assembler.toModel(product);
    }

    @PostMapping(path = "/products")
    public ResponseEntity<EntityModel<Product>> createProduct(@RequestBody Product productData) {

        EntityModel<Product> newProduct = assembler.toModel(productRepository.save(productData));

        return ResponseEntity
                .created(newProduct.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(newProduct);
    }

    @PutMapping(path = "/products/{id}")
    public ResponseEntity<EntityModel<Product>> updateProduct(@PathVariable Long id, @RequestBody Product productData) {
        Product productUpdated = productRepository.findById(id)
                .map(product -> {
                    product.setName(productData.getName());
                    product.setPrice(productData.getPrice());
                    product.setInStock(productData.getInStock());

                    return productRepository.save(product);
                })
                .orElseGet(() -> {
                    productData.setId(id);

                    return productRepository.save(productData);
                });

        EntityModel<Product> entityModel = assembler.toModel(productUpdated);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping(path = "/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
