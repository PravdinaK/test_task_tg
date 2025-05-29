package test_task.service;

import test_task.dto.UserViewDto;

public interface AuthWebService {

    UserViewDto createUser(String initDataRaw);
}