package ru.practicum.ewm.user.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.reasons_and_messages.ExceptionMessages;
import ru.practicum.ewm.participation_request.ParticipationRequestRepository;
import ru.practicum.ewm.participation_request.entity.ParticipationRequestStatus;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    private final UserMapper userMapper;

    @Transactional
    public UserDto create(NewUserRequest dto) {
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    @Transactional(readOnly = true)
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAllOrderedById(from, size);
        } else {
            users = userRepository.findAllOrderedByIdAndIdsIn(from, size, ids);
        }
        return userMapper.toUserDtos(users);
    }

    @Transactional
    public void delete(long userId) {
        int updated = userRepository.safeDelete(userId, LocalDateTime.now());
        if (updated == 0) {
            throw new NotFoundException(String.format(ExceptionMessages.USER_NOT_FOUND, userId));
        }
        participationRequestRepository.updateParticipationRequestsStatusByRequesterId(
                userId,
                ParticipationRequestStatus.PENDING,
                ParticipationRequestStatus.CANCELED);
    }
}
