var ExamApp = (function ($, rivets, _) {

    var app = {

        exam: null,

        init: function (exam) {

            exam.questions = _.map(exam.questions, function (item, index) {
                return $.extend(true, item, {
                    choiceSelected: false,
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
                    app.exam.questions[question].choiceSelected = model.choice.id;
                    app.exam.questions[question].isAnswerCorrect = model.choice.is_answer
                }
            }
        },

        validate: function () {

            var answers = _.map(app.exam.questions, function (question, index) {

                app.exam.questions[index].invalidChoice = typeof question.choiceSelected === 'boolean';
                return {
                    question_id: question.id,
                    choice_id: question.choiceSelected,
                    is_correct: question.isAnswerCorrect,
                    allocated_marks: question.allocated_marks
                }
            });

            var examStats = _.reduce(answers,
                function (result, value, key) {
                    var isValid = result.isValid && !(typeof value.choice_id === 'boolean');
                    var totalMarks = result.totalMarks + value.is_correct && value.allocated_marks ? value.allocated_marks : 0;
                    return {
                        isValid: isValid,
                        totalMarks: totalMarks
                    };
                }, {
                    isValid: true,
                    totalMarks: 0
                });

            return {
                isValid: examStats.isValid,
                totalMarks: examStats.totalMarks,
                passed: app.exam.passmark ? totalMarks >= app.passmark : true,
                answers: answers,
                message: examStats.isValid ? "Successful" : "Please check the errors and resubmit"
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