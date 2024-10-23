package com.sky.mapper;

import com.sky.entity.Employee;
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
    @Insert("insert into employee(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "VALUES(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);

    /**
     * 查询员工数量
     */
    Long count(String name);

    /**
     * 员工分页查询
     */
    List<Employee> page(int start, int pageSize, String name);

    /**
     * 根据id动态修改员工
     */
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     */
    @Select("select * from employee where id=#{id}")
    Employee getById(Long id);
}
