var usernamePage = document.querySelector('#username-page');
var usernameForm = document.querySelector('#usernameForm');

var stompClient = null;
var username = null;

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.removeUser",
        {},
        JSON.stringify({adminCommand: username, type: 'KICK'})
    );
}
function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function disconnect(event){
    stompClient.disconnect();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);
    var error = message.errorMessage;
    if(error){
        alert(error);
    }else{
        alert(message.adminCommand+ ' has been kicked');
    }
    disconnect();
}

usernameForm.addEventListener('submit', connect, true);