package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     */
    @AutoFill(OperationType.INSERT)
    @Insert("insert into employee(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "VALUES(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void add(Employee employee);

    /**
     * 根据姓名查询员工数量
     */
    Long count(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 员工分页查询
     */
    List<Employee> page(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据id动态修改员工
     */
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     */
    @Select("select * from employee where id=#{id}")
    Employee getById(Long id);
}
