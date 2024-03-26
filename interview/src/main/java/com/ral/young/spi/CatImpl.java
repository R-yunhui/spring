package com.ral.young.spi;

/**
 *
 * @author renyunhui
 * @date 2024-03-15 9:39
 * @since 1.0.0
 */
public class CatImpl implements IAnimal {

    @Override
    public void sayName() {
        System.out.println("I AM CAT");
    }
}
