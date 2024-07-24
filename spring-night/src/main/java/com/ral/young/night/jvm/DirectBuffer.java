package com.ral.young.night.jvm;

import sun.misc.Cleaner;

import java.nio.ByteBuffer;

/**
 * 直接内存
 *
 * @author renyunhui
 * @date 2024-07-24 10:44
 * @since 1.0.0
 */
public class DirectBuffer {

    public static void main(String[] args) {
        /*
         *  NIO用堆外内存的原因
         *   1. 减少垃圾回收压力：在传统的Java I/O中，使用的是堆内存，而堆内存的垃圾回收是由JVM自动管理的。
         * 大量频繁的垃圾回收会导致应用程序的暂停和性能下降。而使用堆外内存，则可以避免这种情况，因为堆外内存不受JVM垃圾回收的影响。
         *
         *   2.提高I/O性能：堆外内存是直接与操作系统交互的内存，可以通过零拷贝（Zero-Copy）技术将数据从磁盘或网络读取到堆外内存，
         * 然后直接与应用程序进行数据交换，避免了数据在堆内存和堆外内存之间的复制过程。这样可以显著提高I/O性能，尤其是在处理大量数据时。
         *
         *   3.避免堆内存限制：堆内存是有一定的限制的，堆内存不足可能会导致OutOfMemoryError。
         * 使用堆外内存可以在一定程度上规避这个限制，因为它不在Java堆中，不受堆大小限制，只受系统可用内存的影响。
         */
        // 分配 1M 的直接内存，不占用堆内存
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        String msg = "hello world";
        byteBuffer.put(msg.getBytes());

        // 堆上分配 1M 的内存
        ByteBuffer headByteBuffer = ByteBuffer.allocate(1024);
        headByteBuffer.put(msg.getBytes());

        // 由于堆外内存不受Java垃圾回收机制管理，需要手动释放内存资源，避免内存泄漏。
        // 通过调用ByteBuffer的cleaner()方法获取Cleaner对象，并调用其clean()方法来释放堆外内存。
        Cleaner cleaner = ((sun.nio.ch.DirectBuffer) byteBuffer).cleaner();
        cleaner.clean();
    }
}
