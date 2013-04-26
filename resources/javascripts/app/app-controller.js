app.controller("AppCtrl", ["$scope", "$routeParams", "$http", function ($scope, $routeParams, $http) {
  $scope.data = {
    test: "Azerty",
    number: 51
  }

  $scope.signinForm = {
    username: "",
    password: ""
  }

  $scope.registerForm = {
    username: "",
    password: "",
    email: ""
  }

  $scope.signin = function () {
    console.log("SIGN IN 1");
    console.log($scope.signinForm);
  }

  $scope.register = function () {
    console.log("REGISTER");
    $http.post("/api/v1/users", $scope.registerForm);
    console.log($scope.registerForm);
  }
}])