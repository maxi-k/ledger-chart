(function($) {

    window.Lens = function(data) {
        var self = this;
        if ($.isArray(data)) {
            this.array = data;
        } else {
            this.array = data.split(':');
        }

        this.encode = function() {
            var s = "";
            $.map(self.array, function(item, idx) {
                s += item;
                s += ":";
            });
            return s.slice(0, -1) + "";
        };
    };

    window.AccountTree = function(data) {
        var self = this;
        this.raw = data;

        this.build_navigation_root = function() {
            return self.build_navigation(self.raw);
        };

        this.build_navigation = function(input) {
            var str = "<ul>";
            $.each(input, function(idx, item) {
                var name = item.name,
                    fullname = item['full-name'];
                if (name == null || name == 'null') name = 'Root';
                if (fullname == null || fullname == 'null') fullname = '';
                str += '<li>';
                str += "<a href='#' data-account-id='" + fullname + "'>";
                str += '<span>' + name + '</span>';
                str += '</a>';
                if (item.accounts.length > 0) {
                    str += self.build_navigation(item.accounts);
                }
                str += '</li>';
            });
            str += '</ul>';
            return str;
        };

        this.get_subamounts = function() {
            var list = null;
            var hasTop = false;
            if (self.raw[0].accounts.length == 0) { list = self.raw; }
            else { hasTop = true; list = self.raw[0].accounts; }

            var sum = 0;
            var amounts = hasTop ? [0] : [];
            var labels = hasTop ? ["Rest"] : [];
            $.each(list, function(idx, item) {
                sum += Math.abs(item.amount);
                labels.push(item.name);
                amounts.push(Math.abs(item.amount));
            });

            if (hasTop) {
                var a = self.raw[0].amount;
                if (a > 0) {
                    amounts[0] = Math.abs(self.raw[0].amount - sum);
                } else if (a < 0) {
                    // amounts[0] = Math.abs(self.raw[0].amount + sum);
                }
                if (amounts[0] == 0) { amounts.shift(); labels.shift(); };
            }
            return [amounts, labels];
        };

        this.get_by_lens = function(lens) {
            var result = self.raw;
            if (lens.length == 0) return result;
            var mlens = Array.prototype.reverse.call(lens);
            while(mlens.length > 0) {
                var searched = mlens.pop();
                var cur_elem = result.accounts;
                var flag = false;
                for(var i = 0; i < cur_elem.length; ++i) {
                    if (cur_elem[i].name == searched) {
                        result = cur_elem[i];
                        flag = true;
                        break;
                    }
                }
                if (flag == false) break;
            }
            return result;
        };

    };

    window.LedgerCanvas = function(parent, type_selector_wrapper) {
        var self = this;
        this.parent = parent;
        parent.html("<canvas id='chart'></canvas>");
        this.canvas = $('#chart');
        this.type_selector_wrapper = type_selector_wrapper;

        this.chart = null;
        this.chart_options = null;
        this.chart_type = 'bar';
        this.chart_types = [self.chart_type, 'pie', 'polarArea'];

        this.colors = [
            "#AA7939", "#1C1813", "#634E32", "#F19A27", "#FF9200",
            "#AA6339", "#1C1613", "#634432", "#F17227", "#FF5F00",
            "#AA8939", "#1C1913", "#635532", "#F1B727", "#FFB600",
            "#AA7B39", "#3F2909", "#75501C", "#DFAA60", "#FFCD86",
            "#AA6539", "#3F1E09", "#753F1C", "#DF9260", "#FFB586",
            "#AA8A39", "#3F3009", "#755C1C", "#DFBB60", "#FFDD86"
        ];


        this.type_selector_wrapper.html("<select id='chart-type-select'></select>");
        this.type_selector = $('#chart-type-select');

        this.delete_chart = function() {
            if (self.canvas != null) {
                var c = self.canvas[0];
                c.getContext('2d').clearRect(0, 0, c.width, c.height);
            }
            if (self.chart != null) {
                self.chart.destroy();
            }
        };

        this.reload_chart = function() {
            if (self.chart_options != null) {
                self.delete_chart();
                self.chart_options.type = self.chart_type;
                self.chart = new Chart(self.canvas, self.chart_options);
                self.size_canvas(true);
            }
        };

        this.size_canvas = function(prevent_reload) {
            var width = $(window).innerWidth() - $('#sidebar').outerWidth() - 40,
                height = $(window).innerHeight();
            var c = self.canvas;
            c.width(width);
            c.height(height);
            c[0].width = width;
            c[0].height = height;
            if (typeof(prevent_reload) == 'undefined' || prevent_reload == false) {
                self.reload_chart();
            }
        };
        $(window).resize(function(e) { self.size_canvas(); });
        this.size_canvas();

        this.initialize_type_selector = function(select) {
            var s = '';
            $.each(self.chart_types, function(idx, item) {
                var ss = idx == 0 ? 'selected' : '';
                s += "<option " + ss + " value=" + item + ">" + item + "</option>";
            });
            select.html(s);
            select.change(function(e) {
                self.chart_type = select.find("option:selected").val();
                self.reload_chart();
            });
        };
        this.initialize_type_selector(this.type_selector);

        this.update_chart = function(tree) {
            self.delete_chart();
            var parsed = tree.get_subamounts();
            self.chart_options = {
                type: self.chart_type,
                data: {
                    labels: parsed[1],
                    datasets: [{
                        borderWidth: 0,
                        borderColor: '#333',
                        data: parsed[0],
                        backgroundColor: self.colors,
                        borderColor: self.colors
                    }]
                }
            };
            self.chart = new Chart(self.canvas, self.chart_options);
        };

    };

    window.LedgerChart = function(url, initial_lenses, navigation,
                                  charts_wrapper, chart_selector) {
        var self = this;
        this.url = url;
        this.elems = {};
        this.elems.navigation = navigation;
        this.elems.selected_nav = null;

        this.lenses = initial_lenses;
        this.tree = null;
        this.chart = new LedgerCanvas(charts_wrapper, chart_selector);

        this.encode_lenses = function(lenses) {
            return $.map(self.lenses, function(lens, index) {
                return lens.encode();
            });
        };

        this.setup_navigation = function() {
            self.elems.navigation.html(self.tree.build_navigation_root());
            self.link_navigation();
        };

        this.link_navigation = function() {
            self.elems.navigation.find('a').click(function(e) {
                var target = $(e.delegateTarget);
                if (self.elems.selected_nav != null)
                    self.elems.selected_nav.attr("id", "");
                self.elems.selected_nav = target.closest("li");
                self.elems.selected_nav.attr("id", "selected-nav");
                self.on_navigation(target.data('account-id'));
            });
        };

        this.on_navigation = function(account) {
            self.lenses = [new Lens(account)];
            self.fetch_data(self.update_view, self.display_failure);
        };

        this.update_view = function(rawdata) {
            var data = $.isArray(rawdata[0]) ? rawdata[0] : rawdata;
            self.tree = new AccountTree(data);
            self.chart.update_chart(self.tree);
            return data;
        };

        this.update_view_initial = function(rawdata) {
            self.update_view(rawdata);
            self.setup_navigation();
        };

        this.display_failure = function(data) {

        };

        this.fetch_data = function(success, failure) {
            if (typeof(success) === 'undefined') { success = self.update_view; }
            if (typeof(failure) === 'undefined') { failure = self.display_failure; }
            $.ajax({
                type: 'POST',
                url: self.url,
                dataType: "json",
                data: { lenses: self.encode_lenses(self.lenses) }
            }).done(success).fail(failure);
        };
    };

    $(function($) {
        var ledgerChart = new LedgerChart('data', [new Lens([])],
                                          $('#navigation'), $('#charts-js-wrapper'),
                                          $('#chart-selector'));
        window.ledgerChart = ledgerChart;
        ledgerChart.fetch_data(ledgerChart.update_view_initial,
                               ledgerChart.display_failure);
    });

})(jQuery);
