package com.example.spring.services;

import com.example.spring.entities.Product;
import com.example.spring.entities.User;
import com.example.spring.exceptions.ProductErrorException;
import com.example.spring.repositories.ProductRepository;
import com.example.spring.requests.UpdateProductRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    /**
     * Get products related to the provided user
     *
     * @param user the seller user object
     * @return List<Product> Products sold by the user
     */
    public List<Product> getProducts(User user) {
        return productRepository.findAllBySeller(user);
    }

    /**
     * Creates a product for the request seller
     * allowed cost {5, 10, 20, 50, 100}
     *
     * @param user the seller user
     * @param name product name
     * @param amountAvailable product stock
     * @param cost product cost
     * @return Product
     */
    public Product createProduct(User user, String name, Integer amountAvailable, Integer cost) {
        Integer[] costs = {5, 10, 20, 50, 100};

        if(!Arrays.asList(costs).contains(cost)){
            throw new ProductErrorException("5, 10, 20, 50 and 100 are the only allowed amounts for product cost");
        }

        Product product = new Product();
        product.setName(name);
        product.setAmountAvailable(amountAvailable);
        product.setCost(cost);
        product.setSeller(user);

        productRepository.save(product);

        return product;
    }

    /**
     * deletes the product and also checks if the requested product to delete has the authenticated
     * user as the seller since a seller can only delete his products
     *
     * @param user product seller
     * @param productId product id
     * @return Product the deleted product
     */
    public Product deleteProduct(User user, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductErrorException("product not found"));

        if (!Objects.equals(user, product.getSeller())) {
            throw new ProductErrorException("only the user who created this product can delete it");
        }

        productRepository.delete(product);

        return product;
    }

    /**
     * updates a product for the authenticated seller only
     *
     * @param user seller
     * @param productId product id
     * @param updateProductRequest product update request body
     * @return Product the updated product
     */
    public Product updateProduct(User user, Long productId, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductErrorException("product not found"));

        //check if the authenticated user created this product or not
        if (user != product.getSeller()) {
            throw new ProductErrorException("only the user who created this product can update it");
        }

        //update product name only if it didn't change
        if (updateProductRequest.getName() != null &&
                updateProductRequest.getName().length() > 0 &&
                !Objects.equals(updateProductRequest.getName(), product.getName())) {
            product.setName(updateProductRequest.getName());
        }

        //update product cost only if it didn't change
        if (updateProductRequest.getCost() != null &&
                !Objects.equals(updateProductRequest.getCost(), product.getCost())) {
            product.setCost(updateProductRequest.getCost());
        }

        //update product stock only if it didn't change
        if (updateProductRequest.getAmountAvailable() != null &&
                !Objects.equals(updateProductRequest.getAmountAvailable(), product.getAmountAvailable())) {
            product.setAmountAvailable(updateProductRequest.getAmountAvailable());
        }

        productRepository.save(product);

        return product;
    }

    public void updateProductStock(Product product, Integer quantity){
        int productStock = product.getAmountAvailable();
        if (productStock < quantity) {
            throw new ProductErrorException("request quantity is higher than the available product stock");
        }

        product.setAmountAvailable(productStock - quantity);

        productRepository.save(product);

    }
}
