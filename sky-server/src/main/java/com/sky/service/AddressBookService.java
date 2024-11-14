package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {

    /**
     * 新增地址
     */
    void add(AddressBook addressBook);

    /**
     * 查询用户所有地址
     */
    List<AddressBook> list();

    /**
     * 查询用户默认地址
     */
    AddressBook getDefault();

    /**
     * 根据主键id修改地址
     */
    void update(AddressBook addressBook);

    /**
     * 根据主键id查询地址
     */
    AddressBook getById(Long id);

    /**
     * 根据主键id删除地址
     */
    void deleteById(Long id);

    /**
     * 设置默认地址
     */
    void setDefault(AddressBook addressBook);
}
