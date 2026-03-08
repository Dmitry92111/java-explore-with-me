package ru.practicum.ewm.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.NewCommentDto;
import ru.practicum.ewm.user.UserMapper;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class})
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "deletedOn", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment fromNewCommentDto(NewCommentDto dto);

    CommentDto toDto(Comment comment);

    List<CommentDto> toDtos(List<Comment> comments);
}
