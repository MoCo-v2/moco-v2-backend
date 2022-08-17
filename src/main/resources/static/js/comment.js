
function saveComment(){
    /* 게시글 번호 */
    const boardId = document.getElementById("boardId").value;
    /* 댓글 내용 */
    const comment = document.getElementById("comment").value;

    /* 공백 및 빈 문자열 체크 */
    if(!comment || comment.trim() === "") {
        alert("공백 또는 빈 문자열은 입력하실수 없습니다.");
        return false;
    }else {
        /* ajax */
        let csrf = document.getElementsByTagName('meta').item(name='_csrf').getAttribute("content");

        /* 현재 헤더 인스턴스 생성 */
        const myHeaders = new Headers();
        myHeaders.set("X-CSRF-TOKEN",csrf);

        const baseUrl = "http://localhost:8080";
        /* XMLHttpRequest 객체 정의 */
        httpRequest = new XMLHttpRequest();

        /* 입력된 데이터 Json 형식으로 변경 */
        var reqJson = new Object();
        reqJson.comment = comment;
        reqJson.boardId = boardId;

        /* POST 방식으로 요청 */
        httpRequest.open('POST', baseUrl+"/board/comment/"+boardId);
        /* 요청 Header에 컨텐츠 타입은 Json으로 사전 정의 */
        httpRequest.setRequestHeader('Content-Type', 'application/json');
        /* ResponseType Json */
        httpRequest.responseType = "json";

        /* 정의된 서버에 Json 형식의 요청 Data를 포함하여 요청을 전송 */
        httpRequest.send(JSON.stringify(reqJson));

        /* httpRequest 상태 감지 */
        httpRequest.onreadystatechange = () => {
            /* readyState가 Done이고 응답 값이 200(ok) 일때 받아온 boolean으로 분기 */
            if(httpRequest.readyState === XMLHttpRequest.DONE) {
                if(httpRequest.status === 200) {
                    let result = httpRequest.response;
                    console.log(result)
                    alert("댓글이 등록되었습니다.");
                    window.location.reload();
                }else{
                    let error = httpRequest.response;
                    console.log(error.message);
                }
            }
        }
    }
}