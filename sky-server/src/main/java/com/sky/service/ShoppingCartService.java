package com.sky.service;


import com.sky.dto.ShoppingCartDTO;

/**
 * 购物车service
 */
public interface ShoppingCartService {

    /**
     * 添加到购物车
     * @param shoppingCartDTO
     */
    void addshoppingCart(ShoppingCartDTO shoppingCartDTO);
}
