package com.gregoryvicent.restapispringcrud02.reponsitory;

import com.gregoryvicent.restapispringcrud02.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
