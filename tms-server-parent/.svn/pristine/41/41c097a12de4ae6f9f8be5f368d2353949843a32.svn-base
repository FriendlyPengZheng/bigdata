/**
 * @fileOverview 表格热力图
 * @name heatmap.js
 * @author Maverick youngleemails@gmail.com
 */

/**
 * 管理中添加的的热力图方法
 * @param { Object } body 表格 tbody
 * @param { Object } con 容器
 * @returns {}
 */
window.setThermodynamic = function(body, con) {
    var rules = {    // Can apply columns and rows
        // cols: [0, 1],    // 想要屏蔽的列
        // rows: [2, 3],    // 想要屏蔽的行
        regexp: /\d%$/,    // 根据正则表达式屏蔽
        range: "numeric",    // default 采用 0％ 到 100％ 的区间长度 / numeric 采用表格中最小值和最大值作为区间长度
        slice: { "slicing": 20 },    // 区间分段数
        order: "asc",    // 颜色排序正序或逆序
        colorRange: ["rgb(219, 238, 243)", "rgb(75, 172, 198)"]    // 由浅到深的颜色区间
        // colorRange: ["rgb(254, 241, 223)", "rgb(254, 143, 0)"]    // 由浅到深的颜色区间
    };

    if ("热力图" == con.parent().parent().parent().find(':selected').val()) {
        tableHeatMap(body, rules);
    } else if ("数据" == con.parent().parent().parent().find(':selected').val()) {
        con.find('td').css("background", "");
    } else {
        if ("热力图" == con.parent().parent().parent().parent().parent().find(':selected').val()) {
			if (con.parent().parent().parent().find('.mod-header').length == 0)
				tableHeatMap(body, rules);
        } else if ("数据" == con.parent().parent().parent().parent().parent().find(':selected').val()) {
			if (con.parent().parent().parent().find('.mod-header').length == 0)
				con.find('td').css("background", "");
        }
    }
};

/**
 * 对单个 <td></td> 进行着色
 * @param { Object } tdId td 的 id
 * @param { Function } renderRange 获取着色区间的方法
 * @param { Object } slice 分片长度
 * @param { Function } renderFn 着色方法
 * @param { Function } renderColor 颜色生成器方法
 * @param { String } order 着色深浅顺序
 * @param { Array } colorRange 颜色分布
 */
var tdRendering = function (tdId, renderRange, slice, renderFn, renderColor, order, colorRange) {
    var level = {};

    level = renderColor(renderRange(level, slice),
                        order,
                        colorRange);
    var tdColor = renderFn(level, tdId.text());

    tdId.css("background", tdColor);
};

/**
 * 针对 tbody 根据 rules 进行着色
 * @param { Object } table 原始的 tbody
 * @param { Object } rules 着色规则
 * @returns {}
 */
var tableHeatMap = function (table, rules) {
    if (rules) {
        // Apply rules
        if (rules.cols) {    // Rules on columns
            table.find('tr').each(function () {
                for (var i = 0; i < rules.cols.length; i++) {
                    var markedTd = $(this).find('td').eq(rules.cols[i]);
                    if (!(markedTd.hasClass('x'))) {
                        markedTd.addClass('x');
                    }
                }
            });
        }
        if (rules.rows) {    // Rules on rows
            for (var j = 0; j < rules.rows.length; j++) {
                var markedTr = table.find('tr').eq(rules.rows[j]);
                markedTr.find('td').each(function () {
                    if (!($(this).hasClass('x'))) {
                        $(this).addClass('x');
                    }
                });
            }
        }
        if (rules.regexp) {    // Regular expression support
            table.find('td').each(function () {
                if (!rules.regexp.test($(this).text())) {
                    if (!($(this).hasClass('x'))) {
                        $(this).addClass('x');
                    }
                }
            });
        }
    }

    var tdArray = [],
        opTds = table.find('td').not('.x').filter(function () {
            return $(this).text() !== "-";
        });

    opTds.each(function () {
        tdArray.push(parseFloat($(this).text()));
    });

    tdArray.sort(function (x, y) {
        return (x > y ? 1 : -1);
    });

    var oriSlicing = rules.slice.slicing;
    switch(rules.range) {
    case "default":
        $.extend(rules, {
            slice: { "min": 0, "max": 100, "slicing": oriSlicing}
        });
        break;
    case "numeric":
        $.extend(rules, {
            slice: { "min": Math.floor(tdArray[0]),
                     "max": Math.ceil(tdArray[tdArray.length - 1]),
                     "slicing": oriSlicing}
        });
        break;
    };

    opTds.each(function () {
        tdRendering($(this),
                    getRange, rules.slice,
                    heatmapRender,
                    colorGenerator, rules.order, rules.colorRange);
    });
};

/**
 * 颜色生成器
 * @param { Object } rangeMap 着色区间分布
 * @param { String } order 着色深浅顺序
 * @param { Array } colorRange 颜色分布
 * @returns { Object } 新拼合的着色区间分布
 */
var colorGenerator = function (rangeMap, order, colorRange) {
    var objectLength = function (obj) {
        var len = 0;

        for (var x in obj) {
            len++;
        }

        return len;
    };

    var colorFrom = colorRange[0].slice(4, colorRange[0].length - 1).split(','),
        colorTo   = colorRange[1].slice(4, colorRange[1].length - 1).split(','),
        rLength   = colorTo[0] - colorFrom[0],
        gLength   = colorTo[1] - colorFrom[1],
        bLength   = colorTo[2] - colorFrom[2],
        slice     = objectLength(rangeMap),
        ascStack  = [],
        descStack = [];

    switch (order) {
    case "asc":    // light(small) -> dark(large)
        for (var j = 0; j < slice; j++) {
            var newAscColor = "rgb("
                    + String(Math.round(Number(colorTo[0]) - (rLength / (slice - 1)) * j)) + ", "
                    + String(Math.round(Number(colorTo[1]) - (gLength / (slice - 1)) * j)) + ", "
                    + String(Math.round(Number(colorTo[2]) - (bLength / (slice - 1)) * j))
                    + ")";
            ascStack.push(newAscColor);
        }

        for (x in rangeMap) {
            rangeMap[x] = ascStack.pop();
        }

        break;
    case "desc":    // dark(large) -> light(small)
        for (var i = 0; i < slice; i++) {
            var newDescColor = "rgb("
                    + String(Math.round(Number(colorFrom[0]) + (rLength / (slice - 1)) * i)) + ", "
                    + String(Math.round(Number(colorFrom[1]) + (gLength / (slice - 1)) * i)) + ", "
                    + String(Math.round(Number(colorFrom[2]) + (bLength / (slice - 1)) * i))
                    + ")";
            descStack.push(newDescColor);
        }

        for (x in rangeMap) {
            rangeMap[x] = descStack.pop();
        }

        break;
    };

    return rangeMap;
};

/**
 * 获取着色区间
 * @param { Object } rangeMap 着色区间分布
 * @param { Object } slice 分片长度
 * @returns { Object } 有分偏长度的着色区间分布
 */
var getRange = function (rangeMap, slice) {
    var base = slice["min"];
    var len = Math.max(((slice["max"] - slice["min"]) / slice["slicing"]), 1);

    while(base < slice["max"]) {
        base = Math.round(base + len);
        rangeMap[base] = "";
    }
    rangeMap[slice["max"]] = "";

    return rangeMap;
};

/**
 * 着色方法
 * @param { Object } rangeMap 着色区间分布
 * @param { Number } number 当前 <td></td> 的数值
 * @returns { String } 颜色 RGB 数值
 */
var heatmapRender = function (rangeMap, number) {
    var num = parseFloat(number);    // Percentage number by default for now;

    for (var x in rangeMap) {
        if (num < x) {
            return rangeMap[x];
        }
    }
    return rangeMap[x];
};
