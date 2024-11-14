package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/user/addressBook")
@Tag(name = "C端地址簿管理")
public class AddressBookController {
    private final AddressBookService addressBookService;

    /**
     * 新增地址
     */
    @PostMapping
    @Operation(summary = "新增地址")
    public Result<Object> add(@RequestBody AddressBook addressBook) {
        log.info("[C端]新增地址:{}", addressBook);
        addressBookService.add(addressBook);
        return Result.success();
    }

    /**
     * 查询当前用户所有地址
     */
    @GetMapping("/list")
    @Operation(summary = "查询当前用户所有地址")
    public Result<List<AddressBook>> list() {
        log.info("[C端]查询当前用户所有地址");
        List<AddressBook> list = addressBookService.list();
        return Result.success(list);
    }

    /**
     * 查询当前用户默认地址
     */
    @GetMapping("/default")
    @Operation(summary = "查询当前用户默认地址")
    public Result<AddressBook> getDefault() {
        log.info("[C端]查询当前用户默认地址");
        AddressBook addressBook = addressBookService.getDefault();
        return Result.success(addressBook);
    }

    /**
     * 根据主键id设置默认地址
     */
    @PutMapping("/default")
    @Operation(summary = "根据主键id设置默认地址")
    public Result<Object> setDefault(@RequestBody AddressBook addressBook) {
        log.info("根据主键id设置默认地址: {}", addressBook);
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 根据主键id查询地址
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据主键id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id) {
        log.info("[C端]根据主键id查询地址, id:{}", id);
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 根据主键id修改地址
     */
    @PutMapping
    @Operation(summary = "根据主键id修改地址")
    public Result<Object> update(@RequestBody AddressBook addressBook) {
        log.info("[C端]根据主键id修改地址:{}", addressBook);
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 根据主键id删除地址
     */
    @DeleteMapping("/")
    @Operation(summary = "根据主键id删除地址")
    public Result<Object> deleteById(@RequestParam("id") Long id) {
        log.info("[C端]根据主键id删除地址, id:{}", id);
        addressBookService.deleteById(id);
        return Result.success();
    }

}
