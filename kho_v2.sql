-- 1. Bảng Nhà cung cấp
create table
    suppliers (
        id int auto_increment primary key,
        code varchar(50) null comment 'Mã nhà cung cấp, VD: NCC-001',
        name varchar(255) not null,
        email varchar(150) null,
        phone_number varchar(20) null,
        address text null,
        constraint code unique (code),
        constraint email unique (email)
    );

-- 2. Bảng Sản phẩm (Master data)
create table
    products (
        id int auto_increment primary key,
        sku varchar(100) not null comment 'Mã sản phẩm (VD: SP001)',
        name varchar(255) not null,
        description text null,
        current_stock int default 0 not null comment 'Số lượng tồn kho hiện tại',
        image_url varchar(255) null,
        default_supplier_id int null comment 'Nhà cung cấp chính/mặc định',
        constraint sku unique (sku),
        constraint fk_product_default_supplier foreign key (default_supplier_id) references suppliers (id) on delete set null
    );

-- 3. Bảng Người dùng (Chỉ để đăng nhập)
create table
    users (
        id int auto_increment primary key,
        username varchar(100) not null comment 'Tên đăng nhập',
        email varchar(150) not null,
        full_name varchar(255) null,
        password_hash varchar(255) not null comment 'Mật khẩu đã được băm',
        is_active tinyint (1) default 1 not null,
        created_at timestamp default CURRENT_TIMESTAMP null,
        constraint email unique (email),
        constraint username unique (username)
    );

-- 4. Bảng Phiếu xuất
create table
    export_slips (
        id int auto_increment primary key,
        export_date date not null comment 'Ngày xuất hàng',
        reason text null comment 'Lý do xuất, VD: Bán hàng, Hủy hỏng',
        user_id int null comment 'Người dùng tạo phiếu xuất',
        created_at timestamp default CURRENT_TIMESTAMP null,
        constraint fk_export_slips_user foreign key (user_id) references users (id) on delete set null
    );

create index idx_export_user_id on export_slips (user_id);

-- 5. Bảng Chi tiết Phiếu xuất
create table
    export_slip_details (
        id int auto_increment primary key,
        export_slip_id int not null,
        product_id int not null,
        quantity int not null comment 'Số lượng xuất',
        constraint fk_export_detail_product foreign key (product_id) references products (id),
        constraint fk_export_detail_slip foreign key (export_slip_id) references export_slips (id) on delete cascade,
        check (`quantity` > 0)
    );

create index idx_export_product_id on export_slip_details (product_id);

create index idx_export_slip_id on export_slip_details (export_slip_id);

-- 6. Bảng Phiếu nhập
create table
    import_slips (
        id int auto_increment primary key,
        import_date date not null comment 'Ngày nhập hàng',
        supplier_id int null,
        user_id int null comment 'Người dùng tạo phiếu nhập',
        reason text null comment 'Lý do nhập',
        created_at timestamp default CURRENT_TIMESTAMP null,
        constraint fk_import_slips_supplier foreign key (supplier_id) references suppliers (id) on delete set null,
        constraint fk_import_slips_user foreign key (user_id) references users (id) on delete set null
    );

create index idx_import_supplier_id on import_slips (supplier_id);

create index idx_import_user_id on import_slips (user_id);

-- 7. Bảng Chi tiết Phiếu nhập
create table
    import_slip_details (
        id int auto_increment primary key,
        import_slip_id int not null,
        product_id int not null,
        quantity int not null comment 'Số lượng nhập',
        import_price decimal(15, 2) not null comment 'Giá nhập tại thời điểm đó',
        constraint fk_import_detail_product foreign key (product_id) references products (id),
        constraint fk_import_detail_slip foreign key (import_slip_id) references import_slips (id) on delete cascade,
        check (`quantity` > 0),
        check (`import_price` >= 0)
    );

create index idx_import_product_id on import_slip_details (product_id);

create index idx_import_slip_id on import_slip_details (import_slip_id);