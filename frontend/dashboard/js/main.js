$(document).ready(function () {
    $('.chart').easyPieChart({
        //your options goes here
        scaleColor: false,
        scaleLength: 0,
        barColor: "#20C164",
        lineWidth: 10,
        lineCap: "round",
        size: 200
    });
    $('.chart-red').easyPieChart({
        //your options goes here
        scaleColor: false,
        scaleLength: 0,
        barColor: "#F67623",
        lineWidth: 10,
        lineCap: "round",
        size: 200
    });
}); 