package com.example.kho_be.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SupplierDTO {

    private Integer id;

    @NotBlank(message = "Mã nhà cung cấp không được để trống")
    @Pattern(regexp = "^NCC-.{1,8}$", message = "Mã nhà cung cấp phải bắt đầu bằng 'NCC-' và có tối đa 8 ký tự sau đó (VD: NCC-SAMSUNG)")
    private String code;

    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    @Size(max = 255, message = "Tên nhà cung cấp không được vượt quá 255 ký tự")
    private String name;

    @Email(message = "Email không hợp lệ")
    @Size(max = 150, message = "Email không được vượt quá 150 ký tự")
    private String email;

    @Size(max = 20, message = "Số điện thoại không được vượt quá 20 ký tự")
    @Pattern(regexp = "^$|^[0-9+\\-\\s()]+$", message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    private String address;

    // Constructors
    public SupplierDTO() {
    }

    public SupplierDTO(Integer id, String code, String name, String email, String phoneNumber, String address) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
