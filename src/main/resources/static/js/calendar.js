$(document).ready(function() {
    var calendar = $('#calendar').fullCalendar({
        header: {
            left: 'prev,next today',
            center: 'title',
            right: 'month,agendaWeek,agendaDay'
        },
        defaultDate: new Date(),
        navLinks: true,
        editable: true,
        eventLimit: true,
        events: function(start, end, timezone, callback) {
            $.ajax({
                url: '/calendar/calendarList',
                type: 'GET',
                dataType: 'json',
                data: {
                    start: start.format(),
                    end: end.format()
                },
                success: function(data) {
                    var events = [];
                    $(data).each(function() {
                        // FullCalendar에서 end 날짜를 포함하기 위해 end 날짜를 하루 추가
                        var eventEnd = moment(this.end).add(1, 'days').format('YYYY-MM-DD');
                        events.push({
                            id: this.calendarSeq,
                            title: this.title,
                            start: this.start,
                            end: eventEnd,
                            startTime: this.startTime,
                            endTime: this.endTime,
                            contents: this.contents
                        });
                    });
                    callback(events);
                },
                error: function(xhr, status, error) {
                    alert('Failed to load events: ' + error);
                }
            });
        },
        selectable: true,
        select: function(start, end) {
            $('#eventModal').modal('show');
            $('#eventId').val('');
            $('#eventStartDate').val(start.format('YYYY-MM-DD'));
            $('#eventEndDate').val(end.subtract(1, 'days').format('YYYY-MM-DD')); // end 날짜를 하루 빼서 표시
            $('#eventStartTime').val('');
            $('#eventEndTime').val('');
            $('#eventTitle').val('');
            $('#eventContents').val('');
            $('#saveEvent').off('click').on('click', function() {
                saveEvent(false); // 새로운 이벤트 추가
            });
            $('#deleteEvent').hide();
        },
        eventClick: function(event) {
            $('#eventModal').modal('show');
            $('#eventId').val(event.id);
            $('#eventStartDate').val(event.start.format('YYYY-MM-DD'));
            $('#eventEndDate').val(event.end ? event.end.subtract(1, 'days').format('YYYY-MM-DD') : event.start.format('YYYY-MM-DD'));
            var startTime = event.startTime ? event.startTime.split(' ')[1] : '';
            var endTime = event.endTime ? event.endTime.split(' ')[1] : '';
            $('#eventStartTime').val(startTime);
            $('#eventEndTime').val(endTime);
            $('#eventTitle').val(event.title);
            $('#eventContents').val(event.contents);
            $('#saveEvent').off('click').on('click', function() {
                saveEvent(true); // 기존 이벤트 수정
            });
            $('#deleteEvent').show().off('click').on('click', function() {
                deleteEvent(event.id);
            });
        }
    });

    function saveEvent(isUpdate) {
        var id = $('#eventId').val();
        var title = $('#eventTitle').val();
        var contents = $('#eventContents').val();
        var start = $('#eventStartDate').val();
        var end = $('#eventEndDate').val();
        var startTime = $('#eventStartTime').val();
        var endTime = $('#eventEndTime').val();

        console.log('Title:', title);
        console.log('Start Date:', start);
        console.log('End Date:', end);
        console.log('Start Time:', startTime);
        console.log('End Time:', endTime);

        if (!title || !start || !end) {
            $('#errorMessage').text('Title, start date, and end date are required.');
            return;
        }

        // startTime과 endTime을 start와 end에 결합하여 datetime 형식으로 변환
        var startDateTime = start + ' ' + (startTime || '00:00:00');
        var endDateTime = end + ' ' + (endTime || '23:59:59');

        var url = isUpdate ? '/calendar/calendarUpdate' : '/calendar/insertCalendar';
        var data = {
            title: title,
            start: start,
            end: end,
            startTime: startDateTime,
            endTime: endDateTime,
            time: startDateTime,
            contents: contents
        };
        if (isUpdate) data.calendarSeq = id;

        console.log('Sending data:', data); // 로그 추가

        $.ajax({
            url: url,
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(response) {
                if (response.result > 0) {
                    alert(response.msg);
                    calendar.fullCalendar('refetchEvents');
                    $('#eventModal').modal('hide');
                } else {
                    $('#errorMessage').text(response.msg);
                }
            },
            error: function() {
                $('#errorMessage').text('Error occurred while saving event.');
            }
        });
    }

    function deleteEvent(id) {
        if (!confirm('일정을 삭제하시겠습니까?')) {
            return;
        }
        $.ajax({
            url: '/calendar/calendarDelete',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ calendarSeq: id }),
            success: function(response) {
                if (response.result > 0) {
                    alert(response.msg);
                    calendar.fullCalendar('refetchEvents');
                    $('#eventModal').modal('hide');
                } else {
                    $('#errorMessage').text(response.msg);
                }
            },
            error: function() {
                $('#errorMessage').text('Error occurred while deleting event.');
            }
        });
    }
});