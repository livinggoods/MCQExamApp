var ExamApp = (function ($, rivets, _) {

    var app = {

        exam: null,

        init: function (exam) {

            exam.questions = _.map(exam.questions, function (item, index) {
                return $.extend(true, item, {
                    choiceSelected: false,
                    choiceIdSelected: null,
                    invalidChoice: false,
                    isAnswerCorrect: false,
                    errorMessage: "Please make selection",
                    number: index + 1
                })
            });

            app.exam = exam;
            app.initBindings();
        },

        getController: function () {
            return {
                inputChange: function (e, model) {
                    var choice = model["%choice%"];
                    var question = model['%question%'];
                    app.exam.questions[question].choiceSelected = model.choice.question_choice;
                    app.exam.questions[question].choiceIdSelected = model.choice.id;
                    app.exam.questions[question].isAnswerCorrect = model.choice.is_answer;
                }
            }
        },

        validate: function () {

            var answers = _.map(app.exam.questions, function (question, index) {

                app.exam.questions[index].invalidChoice = typeof question.choiceSelected === 'boolean';
                return {
                    training_exam_id: app.exam.id,
                    trainee_id: app.exam.trainee_id,
                    question_id: question.id,
                    question_score: question.isAnswerCorrect ? question.allocated_marks : 0,
                    country: app.exam.country,
                    answer: question.choiceSelected,
                    is_correct: question.isAnswerCorrect,
                    number: question.number,
                    choice_id: question.choiceIdSelected
                }
            });

            var examStats = _.reduce(answers,
                function (result, value, key) {

                    var invalid = typeof value.answer === 'boolean';
                    var isValid = result.isValid && !invalid;
                    var totalMarks = result.totalMarks + value.is_correct && value.allocated_marks ? value.allocated_marks : 0;

                    if (invalid) {
                        result.message += value.number + ", ";
                    }

                    return {
                        isValid: isValid,
                        totalMarks: totalMarks,
                        message: result.message
                    };
                }, {
                    isValid: true,
                    totalMarks: 0,
                    message: "Please answer questions: "
                });

            return {
                isValid: examStats.isValid,
                totalMarks: examStats.totalMarks,
                passed: app.exam.passmark ? examStats.totalMarks >= app.passmark : true,
                answers: answers,
                message: examStats.message
            };
        },

        initBindings: function () {
            rivets.bind(document.querySelector('#exam'), {
                data: {exam: app.exam, isValid: true},
                controller: app.getController()
            });
        }
    };

    return app;

})(jQuery, rivets, _);

function initialize() {
    var exam = Android.getExam();
    ExamApp.init(JSON.parse(exam));
}

function submit() {
    var results = ExamApp.validate();
    Android.submitAnswers(
        results.isValid,
        results.message,
        JSON.stringify(results.answers),
        results.totalMarks,
        results.passed
    );
}