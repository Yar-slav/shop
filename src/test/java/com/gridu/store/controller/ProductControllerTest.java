package com.gridu.store.controller;

import static com.gridu.store.factory.dto.ProductResponseDtoFactory.createProductResponseDTOs;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gridu.store.dto.request.ProductRequestDto;
import com.gridu.store.dto.response.ProductResponseDto;
import com.gridu.store.model.UserEntity;
import com.gridu.store.model.UserRole;
import com.gridu.store.secure.config.JwtAuthenticationFilter;
import com.gridu.store.service.ProductService;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {

    @MockBean
    private  ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Test
    @SneakyThrows
    void addProduct() {
        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
        ProductRequestDto requestDto = new ProductRequestDto("phone", 10L, 2000);
        ProductResponseDto responseDto = new ProductResponseDto(1L, "phone", 10L, 2000);

        String token = "valid token";
        when(productService.addProduct(requestDto, token)).thenReturn(responseDto);
        mockMvc.perform(post("/products")
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.title").value("phone"))
//                .andExpect(jsonPath("$.quantity").value(10))
//                .andExpect(jsonPath("$.price").value(2000));

        verify(productService).addProduct(requestDto, token);

    }

    @Test
    @SneakyThrows
    void getAllProducts() {
        UserEntity user = new UserEntity(1L, "user@gmail.com", "passwordEncode", UserRole.USER, null);
        List<ProductResponseDto> productResponseDTOs = createProductResponseDTOs();

        when(productService.getAll()).thenReturn(productResponseDTOs);

        mockMvc.perform(get("/products")
                        .with(SecurityMockMvcRequestPostProcessors.user(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andDo(print());
//                .andExpect(jsonPath("$.size()", hasSize(3)))
//                .andExpect(jsonPath("$[0].id()").value(productResponseDtos.get(0).getId()));
//                .andExpect(jsonPath("$[1]").value(productResponseDtos.get(1)))
//                .andExpect(jsonPath("$[2]").value(productResponseDtos.get(2)));
//
//        verify(productService).getAll(authHeader);

    }
}