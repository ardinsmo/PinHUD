var theResult;
var TestObject
var username;
function parse(){
    Parse.initialize("2ICGEDAbu9xpPISmefOo6y4VvEYcw7W0oOF7Lp2T", "6tOKR4v4ERtZhk8VHahuCjoinJzZkTOcUxRPWKc5");
    TestObject = Parse.Object.extend("Boards");
    username=Parse.User.current().get("username");
    document.getElementById("welcome").innerHTML="User: "+username;
    /*var testObject = new TestObject();
    testObject.set("userId","Alex");
    testObject.save(null, {
    success: function(testObject) {
        alert("yay! it worked");
        },
    error: function(testObject, error) {
        // Execute any logic that should take place if the save fails.
        // error is a Parse.Error with an error code and description.
        alert('Failed to create new object, with error code: ' + error.description);
        }
    });*/
    
    var query = new Parse.Query(TestObject);
query.equalTo("userId", username);
query.descending("Repins");
query.find({
  success: function(results) {
     theResult=results;
     
     //if (!results[0].get('username')==null)
        document.getElementById("username").value=results[0].get('username');
     //if (!results[0].get('board')==null)
        document.getElementById("board").value=results[0].get('company_board');
        alert("hellll yeah");
    //alert("Successfully retrieved " + results.length + " scores.");
    // Do something with the returned Parse.Object values
    
    for (var i = 0; i < results.length; i++) { 
      var object = results[i];
      //alert(object.id + ' - ' + object.get('userId'));
    }
    //buildHtmlTable();
    insertTable();
  },
  error: function(error) {
    alert("Error: " + error.code + " " + error.message);
  }
  
});

//alert("completed");
}

function yell(){
//alert("Yelling!");
if (theResult.length==0) {
    alert("none");
}
}

function updateUsername() {
   if (!theResult.length==0) {
   alert("beginning");
        var obj = new TestObject();
        obj.set("username",document.getElementById("username").value);
        obj.set("userId",Parse.User.current().get("username"));
        obj.save();
        alert("done");
        //parse();
    } else {
        var query = new Parse.Query(TestObject);
        query.equalTo("userId", username);
        query.find({
            success: function(results) {
                results[0].set("username",document.getElementById("username").value);
                results[0].save();
            },
            error: function(error) {
                alert("Error: " + error.code + " " + error.message);
            }
        });
    }
}

function updateBoard() {
   if (theResult.length==0) {
   //alert("beginning");
        var obj = new TestObject();
        obj.set("company_board",document.getElementById("board").value);
        obj.set("userId",Parse.User.current().get("username"));
        obj.save();
        alert("done");
        //parse();
    } else {
        var query = new Parse.Query(TestObject);
        query.equalTo("userId", username);
        query.find({
            success: function(results) {
                results[0].set("company_board",document.getElementById("board").value);
                results[0].save();
            },
            error: function(error) {
                alert("Error: " + error.code + " " + error.message);
            }
        });
    }
}

function submit(){
    alert("Submitting...");
    /*alert(document.getElementById("username").innerHTML);
    var obj = TestObject;
    obj.set("userId",username);*/
    //obj.set("username",)
    
}