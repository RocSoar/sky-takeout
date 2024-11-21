package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.page.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     */
    void add(EmployeeDTO employeeDTO);

    /**
     * 员工分页查询
     */
    PageResult<Employee> page(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用、禁用员工账号, status: 0 禁用, 1 启用
     */
    void setStatus(Integer status, Long id);

    /**
     * 根据id查询员工信息
     */
    Employee getById(Long id);

    /**
     * 编辑员工信息
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     */
    void editPassword(PasswordEditDTO passwordEditDTO);
}
