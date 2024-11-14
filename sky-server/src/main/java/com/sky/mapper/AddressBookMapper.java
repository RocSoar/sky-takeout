package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {

    /**
     * 新增地址
     */
    @Insert("insert into address_book(user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label,is_default) " +
            "values (#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
    void add(AddressBook addressBook);

    /**
     * 动态条件查询
     */
    List<AddressBook> dynamicQuery(AddressBook addressBook);

    /**
     * 根据主键id修改地址
     */
    void update(AddressBook addressBook);

    /**
     * 根据主键id删除地址
     */
    @Delete("delete from address_book where id=#{id}")
    void deleteById(Long id);

    /**
     * 根据userId设置非默认地址
     */
    @Update("update address_book set is_default=0 where user_id=#{userId}")
    void setNoDefaultByUserId(Long userId);
}
