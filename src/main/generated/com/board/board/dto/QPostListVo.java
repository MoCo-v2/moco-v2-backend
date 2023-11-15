package com.board.board.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.board.board.dto.QPostListVo is a Querydsl Projection type for PostListVo
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QPostListVo extends ConstructorExpression<PostListVo> {

    private static final long serialVersionUID = 278195939L;

    public QPostListVo(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<java.time.LocalDateTime> created_date, com.querydsl.core.types.Expression<String> title, com.querydsl.core.types.Expression<String> writer, com.querydsl.core.types.Expression<String> content, com.querydsl.core.types.Expression<Long> userId, com.querydsl.core.types.Expression<Integer> view, com.querydsl.core.types.Expression<String> thumbnail, com.querydsl.core.types.Expression<String> subcontent, com.querydsl.core.types.Expression<Integer> likecnt, com.querydsl.core.types.Expression<Integer> commentcnt, com.querydsl.core.types.Expression<String> picture, com.querydsl.core.types.Expression<String> hashTag, com.querydsl.core.types.Expression<Boolean> isfull) {
        super(PostListVo.class, new Class<?>[]{long.class, java.time.LocalDateTime.class, String.class, String.class, String.class, long.class, int.class, String.class, String.class, int.class, int.class, String.class, String.class, boolean.class}, id, created_date, title, writer, content, userId, view, thumbnail, subcontent, likecnt, commentcnt, picture, hashTag, isfull);
    }

}

