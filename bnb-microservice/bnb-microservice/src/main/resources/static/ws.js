var socket = new SockJS('http://localhost:8080/chat');
var stompClient = Stomp.over(socket);
stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/exchange-rates', function(messageOutput) {
        var jsonObject = JSON.parse(messageOutput.body);
    });
});
function sendMessage(message) {
    stompClient.send("/app/send/message", {}, JSON.stringify(message));
}
