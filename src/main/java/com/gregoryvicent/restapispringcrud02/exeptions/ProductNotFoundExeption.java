package com.gregoryvicent.restapispringcrud02.exeptions;

public class ProductNotFoundExeption extends RuntimeException {

    public ProductNotFoundExeption(Long id) {
        super("Producto no encontrado, Id del producto: " + id);
    }
}
