package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.page.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@Slf4j
@RequiredArgsConstructor
@RestController("adminEmployeeController")
@RequestMapping("/admin/employee")
@Tag(name = "员工相关接口")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final JwtProperties jwtProperties;

    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "员工退出")
    public Result<String> logout() {
        log.info("员工退出登录");
        return Result.success();
    }

    /**
     * 新增员工
     */
    @PostMapping
    @Operation(summary = "新增员工")
    public Result<Object> add(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工:{}", employeeDTO);
        employeeService.add(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     */
    @GetMapping("/page")
    @Operation(summary = "员工分页查询")
    public Result<PageResult<Employee>> page(@RequestParam(required = false) String name, Integer page, Integer pageSize) {
        EmployeePageQueryDTO pageQueryDTO = new EmployeePageQueryDTO(page, pageSize, name);
        log.info("员工分页查询, 参数:{}", pageQueryDTO);
        PageResult<Employee> pageResult = employeeService.page(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用、禁用员工账号, status: 0 禁用, 1 启用
     */
    @PostMapping("/status/{status}")
    @Operation(summary = "启用禁用员工账号, status: 0 禁用, 1 启用")
    public Result<Object> setStatus(@PathVariable Integer status, Long id) {
        log.info("启用禁用员工账号,id:{}, status:{}", id, status);
        employeeService.setStatus(status, id);
        return Result.success();
    }

    /**
     * 根据id查询员工信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询员工信息")
    public Result<Employee> getById(@PathVariable Long id) {
        log.info("根据id查询员工信息,id:{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     */
    @PutMapping
    @Operation(summary = "编辑员工信息")
    public Result<Object> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息:{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/editPassword")
    @Operation(summary = "修改密码")
    public Result<Object> editPassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        log.info("修改密码:{}", passwordEditDTO);
        employeeService.editPassword(passwordEditDTO);
        return Result.success();
    }
}
