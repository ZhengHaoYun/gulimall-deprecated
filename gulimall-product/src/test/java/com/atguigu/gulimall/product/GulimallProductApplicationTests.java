package com.atguigu.gulimall.product;

import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Test
    public void test() {
        BrandEntity entity = new BrandEntity();
//        entity.setName("华为");
//        brandService.save(entity);
//        System.out.println("保存成功");

//        entity.setBrandId(1L);
//        entity.setDescript("插入测试");
//        entity.setName("插入测试name");
//        brandService.updateById(entity);
//        BrandEntity entity1 = brandService.getOne(new QueryWrapper<BrandEntity>().eq("brand_id", 1));
//        System.out.println(entity1);
        brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id",1)).forEach(System.out::println);


    }

}