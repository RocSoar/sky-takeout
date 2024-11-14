package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.exception.AddressBookBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressBookServiceImpl implements AddressBookService {
    private final AddressBookMapper addressBookMapper;

    /**
     * 新增地址
     */
    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.add(addressBook);
    }

    /**
     * 查询用户所有地址
     */
    @Override
    public List<AddressBook> list() {
        return addressBookMapper.dynamicQuery(AddressBook.builder()
                .userId(BaseContext.getCurrentId()).build());
    }

    /**
     * 查询用户默认地址
     */
    @Override
    public AddressBook getDefault() {
        List<AddressBook> list = addressBookMapper.dynamicQuery(AddressBook.builder()
                .userId(BaseContext.getCurrentId())
                .isDefault(1).build());
        if (list == null || list.isEmpty())
            throw new AddressBookBusinessException(MessageConstant.DEFAULT_ADDRESS_BOOK_NOT_EXIST);
        return list.get(0);
    }

    /**
     * 根据主键id修改地址
     */
    @Override
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据主键id查询地址
     */
    @Override
    public AddressBook getById(Long id) {
        List<AddressBook> list = addressBookMapper.dynamicQuery(AddressBook.builder()
                .id(id).build());
        return list.get(0);
    }

    /**
     * 根据主键id删除地址
     */
    @Override
    public void deleteById(Long id) {
        addressBookMapper.deleteById(id);
    }

    /**
     * 根据主键id设置默认地址
     */
    @Override
    public void setDefault(AddressBook addressBook) {
//        将当前用户的所有地址重置为非默认地址
        addressBookMapper.setNoDefaultByUserId(BaseContext.getCurrentId());

//        根据主键id设置默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }
}
