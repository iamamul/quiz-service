package com.amul.quiz_service.service;

import com.amul.quiz_service.dao.QuizDao;
import com.amul.quiz_service.feign.QuestionServiceClient;
import com.amul.quiz_service.model.QuestionWrapper;
import com.amul.quiz_service.model.Quiz;
import com.amul.quiz_service.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionServiceClient questionServiceClient;


    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        List<Integer> questions = questionServiceClient.generateQuestion(category, numQ).getBody();

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);

    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {

        Quiz quiz = quizDao.findById(id).get();
        List<Integer> questionIds = quiz.getQuestionIds();
        ResponseEntity<List<QuestionWrapper>> questions = questionServiceClient.getQuestionFromId(questionIds);
        return questions;
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {

       ResponseEntity<Integer> score = questionServiceClient.getScore(responses);
        return score;
    }
}
