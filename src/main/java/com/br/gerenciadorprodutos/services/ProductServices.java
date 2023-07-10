package com.br.gerenciadorprodutos.services;

import com.br.gerenciadorprodutos.controllers.ProductController;
import com.br.gerenciadorprodutos.dtos.ProductRecordDto;
import com.br.gerenciadorprodutos.models.ProductModel;
import com.br.gerenciadorprodutos.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
@Transactional
public class ProductServices {

    @Autowired
    ProductRepository productRepository;
    public ProductModel saveProduct(ProductRecordDto productRecordDto){
        // inserido apartir do Java 10, usado para facilitar a declaracão do tipo;
        var productModel = new ProductModel();

        // Para fazer a converssão usamos um recurso do spring(BeanUtils.copyProperties), que recebe o que vai ser convertido e tipo para que será convertido.
        BeanUtils.copyProperties(productRecordDto, productModel);

        // Salvar o modelo usando o repositório
        return productRepository.save(productModel);
    }

    public List<ProductModel> listarTodos() {
        List<ProductModel> productsList = productRepository.findAll();

        if (!productsList.isEmpty()) {
            for (ProductModel product : productsList) {
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return productsList;
    }
}
