app.controller("AppCtrl", 
               ["$scope", "$routeParams", "$http", 
                  function ($scope, $routeParams, $http) {
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

}])
