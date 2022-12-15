-- auto-generated definition
create table user
(
    userName        varchar(256)                       null comment '用户昵称',
    userId          bigint auto_increment comment 'id'
        primary key,
    userAccount     varchar(256)                       null comment '账号',
    avatarUrl       varchar(1024)                      null comment '用户头像',
    gender          varchar(10)                        null comment '性别',
    userPassword    varchar(512)                       not null comment '密码',
    userPhone       varchar(128)                       null comment '电话',
    userEmail       varchar(512)                       null comment '邮箱',
    userStatus      int      default 0                 not null comment '状态 0 - 正常',
    createTime      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete        tinyint  default 0                 not null comment '是否删除',
    userRole        int      default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    planetCode      varchar(512)                       null comment '星球编号',
    tag             varchar(1024)                      null comment '标签json列表',
    userLocation    varchar(255)                       null comment '用户地址',
    userDescription varchar(1024)                      null comment '用户描述',
    userSchool      varchar(255)                       null comment '用户学校',
    userAge         int                                null comment '用户年龄'
)
    comment '用户';

create index idx_userId
    on user (userId);

create index index_userId
    on user (userId);



    -- auto-generated definition
    create table team
    (
        t_id          bigint auto_increment comment 'id'
            primary key,
        t_name        varchar(256)                       not null comment '队伍名称',
        t_description varchar(1024)                      null comment '描述',
        t_maxNum      int      default 1                 not null comment '最大人数',
        t_num         int      default 1                 not null comment '当前人数',
        t_expireTime  datetime                           null comment '过期时间',
        t_userId      bigint                             null comment '用户id（队长 id）',
        t_status      int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
        t_password    varchar(512)                       null comment '密码',
        t_createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
        t_updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
        t_isDelete    tinyint  default 0                 not null comment '是否删除',
        t_avatarUrl   varchar(1024)                      null comment '队伍头像'
    )
        comment '队伍';



-- auto-generated definition
create table user_team
(
    id          bigint auto_increment comment 'id'
        primary key,
    userId      bigint                             null comment '用户id',
    teamId      bigint                             null comment '队伍id',
    joinTime    datetime                           null comment '加入时间',
    createTime  datetime default CURRENT_TIMESTAMP null comment '申请时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete    tinyint  default 0                 not null comment '是否删除',
    details     varchar(1024)                      null comment '申请描述',
    applyStatus tinyint  default 0                 null comment '申请状态 0-待申请 1-通过申请 2-拒绝申请'
)
    comment '用户队伍关系';

-- auto-generated definition
create table article
(
    id          int auto_increment comment '文章id'
        primary key,
    userId      int           not null comment '发布人id',
    teamId      int           not null comment '队伍Id',
    mainBody    text          null comment '正文部分',
    likeNum     int default 0 null,
    publishTime datetime      null comment '发布时间'
)
    comment '发布文章表';







