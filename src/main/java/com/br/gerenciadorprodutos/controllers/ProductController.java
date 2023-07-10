package com.br.gerenciadorprodutos.controllers;

import com.br.gerenciadorprodutos.dtos.ProductRecordDto;
import com.br.gerenciadorprodutos.models.ProductModel;
import com.br.gerenciadorprodutos.repositories.ProductRepository;
import com.br.gerenciadorprodutos.services.ProductServices;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Controller
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductServices productServices;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto){
        ProductModel savedProduct = productServices.saveProduct(productRecordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts(){

        List<ProductModel> productsList = productServices.listarTodos();
        // Retorna um a lista de produtos salvos na base de dados, usando productRepository.findAll()
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }

    //Minha URI e composta, recebendo um UUI id para persistencia no banco
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id){

        // 1 -  Faz um select na base de dados com where id = id_salvo;
        // 2 - Adiciona ao product que sera do tipo Optional.
        Optional<ProductModel> productO = productRepository.findById(id);

        // Faz a validação caso o produto não exista na base de dados retorna apenas uma msg
        if (productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        productO.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Product List"));

        // Se o produto existir, retorna 200 Ok e o produto encontrado
        return ResponseEntity.status(HttpStatus.OK).body(productO.get());
    }


    @PutMapping("/products/{id}")
                                                // recebe o Id do produto
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                //recebe no corpo da requisição os campos para alteração
                                                @RequestBody @Valid ProductRecordDto productRecordDto){

        // 1 -  Faz um select na base de dados com where id = id_salvo para ver se recurso existe;
        // 2 - Adiciona ao product que sera do tipo Optional.
        Optional<ProductModel> productO = productRepository.findById(id);

        // Faz a validação caso o produto não exista na base de dados retorna apenas uma msg
        if (productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        // pega o ID para inserir junto aos dados alterados.
        var productModel = productO.get();

        // Faz a conversão do Product dto para Entity ProductModel
        BeanUtils.copyProperties(productRecordDto, productModel);

        // Se o produto existir, retorna 200 Ok e o produto encontrado
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }

    //Minha URI e composta, recebendo um UUI id para persistencia no banco
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id){

        // 1 -  Faz um select na base de dados com where id = id_salvo;
        // 2 - Adiciona ao product que sera do tipo Optional.
        Optional<ProductModel> productO = productRepository.findById(id);

        // Faz a validação caso o produto não exista na base de dados retorna apenas uma msg
        if (productO.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }

        // No meu repository faço o delete do ID passado como parametro
        // Aqui usamos o get(), pois estamos usando Optional
        productRepository.delete(productO.get());

        // Retorno uma menssagem + código 200 OK
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully");
    }

}
