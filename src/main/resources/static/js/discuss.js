function like(btn,entityTpye,entityId,entityUserId) {

    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityTpye,"entityId":entityId,"entityUserId":entityUserId},
        function (data) {
            data = $.parseJSON(data);
            if(data.code==0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==0?'已赞':'未赞');
            }else{
                alert(data.msg)
            }

        }
        );
}