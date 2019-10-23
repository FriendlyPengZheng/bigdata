(function($, undefined) {
$("#J_externalEvents .fc-event").each(function() {
    var eventObject = {
        title: $.trim($(this).text()),
        type: "1"
    };
    $(this).data("eventObject", eventObject);
    $(this).draggable({
        zIndex: 999,
        revert: true,
        revertDuration: 0
    });
});

$("#J_calendar").fullCalendar({
    timezone: "local",
    header: {
        left: false,
        center: "title",
        right: "prev,next today"
    },
    events: {
        url: getUrl("admin", "holiday", "getCalendarEvent"),
        success: function(result) {
            if (result.result == 0) {
                $.each(result.data, function() {
                    this.from = this.start;
                    this.to = this.end;
                });
                return result.data;
            }
            return [];
        }
    },
    editable: true,
    droppable: true,
    drop: function(date) {
        var originalEventObject = $(this).data("eventObject"),
            copiedEventObject = $.extend({}, originalEventObject);
        copiedEventObject.start = date;
        $("#J_calendar").fullCalendar("renderEvent", copiedEventObject, true);
    },
    eventRender: function(event, element) {
        var del = $("<a class='del-btn'>").text("×").attr({
                title: "删除"
            }).on("click", function() {
                var event = {},
                    id = $(this).parent().data("fc-seg").event._id;
                event.eventId = id.indexOf("_fc") !== -1 ? null : id;
                event._id = id;
                Event(event).trash();
                $("#J_calendar").fullCalendar("removeEvents", event.eventId);
            });
        return element.append(del);
    },
    eventAfterRender: function(event) {
        var copiedEventObject = $.extend({}, event),
            start = copiedEventObject.start._d,
            end = copiedEventObject.end ? copiedEventObject.end._d : copiedEventObject.start._d;
        copiedEventObject.eventId = copiedEventObject._id.indexOf("_fc") !== -1 ? null : copiedEventObject._id;
        copiedEventObject.start = start.getFullYear() + "-" + (start.getMonth() + 1).toString().pad(2, "0") + "-" + start.getDate().toString().pad(2, "0");
        copiedEventObject.end = end.getFullYear() + "-" + (end.getMonth() + 1).toString().pad(2, "0") + "-" + end.getDate().toString().pad(2, "0");
        event.from = copiedEventObject.start;
        event.to = copiedEventObject.end;
        Event(copiedEventObject).save();
    }
});

var Event = function(options) {
    if (!(this instanceof Event)) {
        return new Event(options);
    }
    if (options.title) this.title = options.title;
    if (options.eventId) this.eventId = options.eventId;
    if (options._id) this.id = options._id;
    if (options.type) this.type = options.type;
    if (options.start) this.start = options.start;
    if (options.from) this.from = options.from;
    if (options.to) this.to = options.to;
    if (options.end) {
        this.end = options.end;
    } else {
        this.end = this.start;
    }
};
Event.prototype = {
title: null,
id: null,
eventId: null,
type: null,
start: null,
end: null,
from: null,
to: null,
save: function() {
    if (this.start === this.from && this.end === this.to) return;
    var that = this;
    ajax(getUrl("admin", "holiday", "saveCalendarEvent"), {
        event_id: this.eventId,
        type: this.type,
        title: this.title,
        start: this.start,
        end: this.end
    }, function(res) {
        if (res.result != 0) {
            $("#J_calendar").fullCalendar("removeEvents", that.id);
        } else {
            if (!that.eventId) {
                $("#J_calendar").fullCalendar("removeEvents", that.id);
                $("#J_calendar").fullCalendar("renderEvent", {
                    id: res.data,
                    title: that.title,
                    start: that.start,
                    end: that.end,
                    from: that.start,
                    to: that.end,
                    type: that.type
                }, true);
            }
        }
    });
},
trash: function() {
    if (this.id != this.eventId) return;
    ajax(getUrl("admin", "holiday", "delCalendarEvent"), {
        event_id: this.eventId
    }, function(res) {
        if (res.result != 0) {
            window.reload();
        }
    });
}
};
})(jQuery);
