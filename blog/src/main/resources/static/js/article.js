const deleteButton = document.getElementById('delete-btn');
//HTML에서 delete-btn으로 설정한 element를 찾아

if (deleteButton) {
//그 element에서 click 이벤트가 발생한다면
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
//fetch() 메서드를 통해 API를 요청
        fetch(`/api/articles/${id}`, {
            method : 'DELETE'
        })
            //fetch()가 잘 완료되면 연이어 실행
            .then(() => {
                alert('삭제가 완료되었습니다'); //팝업 띄우기
                location.replace('/articles'); //화면을 현재 주소를 기반해 옮겨주는 역할
            });
    });
}

const modifyButton = document.getElementById('modify-btn');

if (modifyButton){
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`api/articles/${id}`, {
            method: 'PUT',
            headers : {
                "Content-Type" : "application/json",
            },
            body : JSON.stringify({
                title : document.getElementById('title').value,
                content : document.getElementById('content').value
            })
        })
            .then(() => {
                alert('수정이 완료되었습니다');
                location.replace(`/articles/${id}`);
            });
    });
}

const createButton = document.getElementById('create-btn')

if (createButton) {
    createButton.addEventListener('click', event => {
        fetch("/api/articles", {
            method : "POST",
            headers : {
                'Content-Type' : "application/json",
            },
            body : JSON.stringify({
                title : document.getElementById("title").value,
                content : document.getElementById("content").value
            }),
        })
            .then(() => {
                alert("등록 완료되었습니다.");
                console.log("!!!")
                location.replace("/articles");
            })
    })
}