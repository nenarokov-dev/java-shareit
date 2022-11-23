package ru.practicum.shareit.pagination;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.RequestParamException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class Pagination<T> {

    public List<T> setPagination(Integer from, Integer size, List<T> itemRequests) {
        if (size!=null&&size<=0){
            String message = "Размер страницы должен быть больше нуля.";
            log.error(message);
            throw new RequestParamException(message);
        }
        if (from!=null&&from<0){
            String message = "Индекс первого элемента страницы не может быть меньше нуля.";
            log.error(message);
            throw new RequestParamException(message);
        }
        if (from==null){
            if (size!=null) {
                return itemRequests.stream().limit(size).collect(Collectors.toList());
            } else
                return itemRequests;
        }
        if (from>itemRequests.size()){
            return Collections.emptyList();
        } else
            return itemRequests.stream().skip(from).limit(size).collect(Collectors.toList());
    }
}
