package com.zholdiyarov.cheater.objects;

import java.net.URI;

/**
 * Created by szholdiyarov on 2/11/16.
 */
public class MultipleQuestionObject {
    private String numberOfQuestion;
    private String answerToTheQuestion;

    public MultipleQuestionObject(String numberOfQuestion, String answerToTheQuestion) {
        this.numberOfQuestion = numberOfQuestion;
        this.answerToTheQuestion = answerToTheQuestion;
    }

    public String getAnswerToTheQuestion() {
        return answerToTheQuestion;
    }



}
