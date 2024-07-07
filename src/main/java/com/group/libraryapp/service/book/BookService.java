package com.group.libraryapp.service.book;

import com.group.libraryapp.domain.book.Book;
import com.group.libraryapp.domain.book.BookRepository;
import com.group.libraryapp.domain.user.User;
import com.group.libraryapp.domain.user.UserRepository;
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistory;
import com.group.libraryapp.domain.user.loanhistory.UserLoanHistoryRepository;
import com.group.libraryapp.dto.book.request.BookCreateRequest;
import com.group.libraryapp.dto.book.request.BookLoanRequest;
import com.group.libraryapp.dto.book.request.BookReturnRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final UserLoanHistoryRepository userLoanHistoryRepository;
    private final UserRepository userRepository;

    public BookService(BookRepository bookRepository, UserLoanHistoryRepository userLoanHistoryRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userLoanHistoryRepository = userLoanHistoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void saveBook(BookCreateRequest request){
        bookRepository.save(new Book(request.getName()));   //객체를 생성하면서 리포지토리에 저장!
    }

    @Transactional
    public void loanBook(BookLoanRequest request) {
        //1. 책 정보 가져온다.
        //2. 대출 기록 확인 후 대출중인지 확인
        //3. 대출 중이라면 예외를 발생시킨다.
        Book book = bookRepository.findByName(request.getBookName())
                .orElseThrow(IllegalArgumentException::new);

        if(userLoanHistoryRepository.existsByBookNameAndIsReturn(request.getBookName(), false)) {
            throw new IllegalArgumentException("진작 대출되어 있는 책 입니다.");
        }

        //유저 정보를 가져온다.
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(IllegalArgumentException::new);
        user.loanBook(book.getName());
        //유저 정보와 책 정보를 기반으로 저장한다.
        //userLoanHistoryRepository.save(new UserLoanHistory(user, book.getName()));
    }

    @Transactional
    public void returnBook(BookReturnRequest request) {
        /**
         *  유저 이름을 갖고 아이디를 찾아야한다.
         */
        //유저 정보를 가져온다.
        User user = userRepository.findByName(request.getUserName())
                .orElseThrow(IllegalArgumentException::new);

        user.returnBook(request.getBookName());

        /**
         * 유저를 찾았으니, 유저 아이디와 책 이름을 통해 대출 기록을 찾는다.

        UserLoanHistory history = userLoanHistoryRepository.findByUserIdAndBookName(user.getId(), request.getBookName())
                .orElseThrow(IllegalArgumentException::new);

         * 반납 처리 하기

        history.doReturn();
         */
        /**
         * 원래 대로라면, 아래 save 처리해야하지만,
         * Transactional 어노테이션을 사용하여, 영속성 컨텍스트는 변경 감지가 있어서 변경됨
         */
        //userLoanHistoryRepository.save(history);

    }

}
