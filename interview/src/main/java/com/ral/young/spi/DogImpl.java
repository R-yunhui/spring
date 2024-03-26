package com.ral.young.spi;

/**
 *
 * @author renyunhui
 * @date 2024-03-15 9:40
 * @since 1.0.0
 */
public class DogImpl implements IAnimal {

    @Override
    public void sayName() {
        System.out.println("I AM DOG");
    }
}
