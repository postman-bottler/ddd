package online.bottler.mapletter.application.validator;

import online.bottler.shared.exception.ApplicationException;

public class PageValidator {

    public static void validMaxPage(int maxPage, int nowPage) {
        if (maxPage < nowPage) {
            throw new ApplicationException("페이지가 존재하지 않습니다.");
        }
    }

    public static void validMinPage(int nowPage) {
        if (nowPage < 1) {
            throw new ApplicationException("페이지가 존재하지 않습니다.");
        }
    }
}
