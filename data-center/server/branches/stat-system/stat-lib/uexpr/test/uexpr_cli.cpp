/* vim: set expandtab tabstop=4 softtabstop=4 shiftwidth=4: */
/**
 * @file uexpr_cli.cpp
 * @author richard <richard@taomee.com>
 * @date 2011-11-25
 */

#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <getopt.h>
#include <errno.h>
#include <readline/readline.h>
#include <readline/history.h>

#include "defines.h"
#include "log.h"
#include "i_config.h"
#include "i_ucount.h"
#include "i_uexpr.h"
#include "i_uexpr_so.h"

struct cmd 
{
	const char *c_name;                       /* name of command */
	const char *c_help;                       /* help string */
	int (*c_handler)(int, char **);     /* function to call */
};

static const struct cmd * get_cmd(char *p_name);
int help(int argc, char **argv);
int quit(int argc, char **argv);
int ucount_write(int argc, char **argv);
int uvalue_write(int argc, char **argv);
int uexpr(int argc, char **argv);

static const char* help_help         = "print local help information";
static const char* quit_help         = "terminate the session and exit";
static const char* ucount_write_help = "write ucount";
static const char* uvalue_write_help = "write uvalue";
static const char* uexpr_help        = "eval uexpr";

static const struct cmd cmdtab[] = {
	{"help", help_help, help},
	{"quit", quit_help, quit},
	{"exit", quit_help, quit},
	{"ucount_write", ucount_write_help, ucount_write},
	{"uvalue_write", uvalue_write_help, uvalue_write},
	{"uexpr", uexpr_help, uexpr},
	{0},
};

/**
 * Slice a string up into argc/argv.
 */
int make_argv(const char *line, char **pp_argv, int *p_argc);

i_config *g_p_config = NULL;

int main(int argc, char **argv)
{
    bool interactive = true;
    char uexpr_script_file[PATH_MAX] = {0};

    int c = 0;
    int digit_optind = 0;
    while (true) {
        int this_option_optind = optind ? optind : 1;
        int option_index = 0;
        static struct option long_options[] = {
            {"help", 1, 0, 0},
            {"file", 1, 0, 'f'},
            {0, 0, 0, 0}
        };

        c = getopt_long(argc, argv, "f:h", long_options, &option_index);
        if (c == -1) {
            break;
        }
        
        switch (c) {
            case 'f':
                interactive = false;
                strcpy(uexpr_script_file, optarg);
                break;
            case 'h':
                fprintf(stdout, "uexpr_cli: -f uexpr_script_file\n");
                return 0;
            default:
                printf("?? getopt returned character code 0%o ??\n", c);
        }
    }

    if (optind < argc) {
        printf("non-option ARGV-elements: ");
        while (optind < argc)
            printf("%s ", argv[optind++]);
        printf("\n");
        return -1;
    }
	
    if (create_config_instance(&g_p_config) != 0) {
		fprintf(stderr, "ERROR: create_config_instance");
		return -1;
	}
	
	char config_file_list[][PATH_MAX] = {{"./uexpr_test.ini"}};
	if (g_p_config->init(config_file_list, sizeof(config_file_list) / sizeof(*config_file_list)) != 0) {
		fprintf(stderr, "ERROR: p_config->init");
		return -1;
	}


    FILE* file = NULL;
    if (!interactive) {
        file = fopen(uexpr_script_file, "rb");
        if (file == NULL) {
            fprintf(stderr, "ERROR: fopen: %s: %s\n", uexpr_script_file, strerror(errno));
            return -1;
        }
    }

	for (;;) {
        char buffer[4096] = {0};
        char* line = NULL;
        if (interactive) {
            line = readline("uexpr_cli> ");
        } else {
            line = fgets(buffer, sizeof(buffer) - 1, file);
            if (line != NULL) {
                line[strlen(line) - 1] = 0;
            }
        }

		if (line == NULL) {
			break;
		}
		if (strlen(line) == 0) {
			continue;
		}

		HIST_ENTRY *p_current_history = history_get(history_length);
		if (p_current_history == NULL || strcmp(p_current_history->line, line) != 0) {
			add_history(line);
		}

		char *cmd_argv[1024] = {0};
		int cmd_argc = sizeof(cmd_argv) / sizeof(*cmd_argv);
		make_argv(line, cmd_argv, &cmd_argc);

        if (cmd_argv[0][0] == '#' || cmd_argv[0][0] == ';') {
            continue;
        }

		const cmd * p_cmd = get_cmd(cmd_argv[0]);
		if (p_cmd == NULL) {
			fprintf(stderr, "?Invalid command\n");
			continue;
		} else if (p_cmd == (cmd *)-1) {
			fprintf(stderr, "?Ambiguous command\n");
			continue;
		}

		p_cmd->c_handler(cmd_argc, cmd_argv);

        if (interactive) {
		    free(line);
        }
	}

    if (file != NULL) {
        fclose(file);
    }
	if (g_p_config->uninit() != 0) {
		fprintf(stderr, "?g_p_config->uninit() != 0\n");
		return -1;
	}
	if (g_p_config->release()) {
		fprintf(stderr, "?g_p_config->release() != 0\n");
		return -1;
	}

	return 0;
}

static const struct cmd * get_cmd(char *p_name)
{
	const char *p, *q;
	const struct cmd *c, *found;
	int nmatches, longest;

	longest = 0;
	nmatches = 0;
	found = 0;
	for (c = cmdtab; ((p = c->c_name), p != NULL); ++c) {
		for (q = p_name; *q == *p; q++, p++) {
			if (*q == 0) {
				return c;
			}
		}
		if (*q == 0) {
			if (q - p_name > longest) {
				longest = q - p_name;
				nmatches = 1;
				found = c;
			} else if ((longest != 0) && (q - p_name == longest)) {
				nmatches++;
			}
		}
	}
	if (nmatches > 1) {
		return (struct cmd *)-1;
	}
	
	return found;
}

int help(int argc, char **argv)
{
	for (int i = 0; i != sizeof(cmdtab) / sizeof(*cmdtab) - 1; ++i) {
		fprintf(stdout, "%-15s%s\n", cmdtab[i].c_name, cmdtab[i].c_help);		
	}

	return 0;
}

int quit(int argc, char **argv)
{
	exit(0);
	return 0;
}

int ucount_write(int argc, char **argv)
{
	if (argc != 5) {
		fprintf(stderr, "ERROR: parameter\nusage: ucount_write report_id day_count ucount 1/0\n");
		return -1;
	}

	int report_id = atoi(argv[1]);
	int day_count = atoi(argv[2]);
	int ucount = atoi(argv[3]);
	int isset = atoi(argv[4]);

	char ucount_report_data_dir[PATH_MAX] = {0};
	char ucount_path_name[PATH_MAX] = {0};
	g_p_config->get_config("uexpr", "ucount_report_data_dir", 
				ucount_report_data_dir, sizeof(ucount_report_data_dir) - 1);
	if (ucount_report_data_dir[strlen(ucount_report_data_dir) - 1] == '/') {
		ucount_report_data_dir[strlen(ucount_report_data_dir) - 1] = '/';
	}
	snprintf(ucount_path_name, sizeof(ucount_path_name) - 1, "%s/map%d_%d", 
				ucount_report_data_dir, report_id, day_count);

	i_ucount *p_ucount = NULL;
	if (create_ucount_instance(&p_ucount) != 0) {
		fprintf(stderr, "ERROR: create_ucount_instance");
		return -1;
	}
	if (p_ucount->init(ucount_path_name, i_ucount::CREATE, 0600) != 0) {
		fprintf(stderr, "ERROR: p_ucount->init(%s, i_ucount::CREATE, 0600)", ucount_path_name);
		p_ucount->release();
		return -1;

	}
	if (p_ucount->set(ucount, isset ? i_ucount::SET : i_ucount::UNSET) != 0) {
		fprintf(stderr, "ERROR: p_ucount->set");
		p_ucount->uninit();
		p_ucount->release();
		return -1;
	}
	if (p_ucount->uninit() != 0) {
		fprintf(stderr, "ERROR: p_ucount->uninit");
		return -1;
	}
	if (p_ucount->release() != 0) {
		fprintf(stderr, "ERROR: p_ucount->release");
		return -1;
	}

	return 0;
}

int uvalue_write(int argc, char **argv)
{
	if (argc != 5) {
		fprintf(stderr, "ERROR: parameter\nusage: uvalue_write report_id day_count key value\n");
		return -1;
	}

	int report_id = atoi(argv[1]);
	int day_count = atoi(argv[2]);
	int key = atoi(argv[3]);
	int value = atoi(argv[4]);

	char uvalue_report_data_dir[PATH_MAX] = {0};
	char uvalue_path_name[PATH_MAX] = {0};
	g_p_config->get_config("uexpr", "uvalue_report_data_dir", 
				uvalue_report_data_dir, sizeof(uvalue_report_data_dir) - 1);
	if (uvalue_report_data_dir[strlen(uvalue_report_data_dir) - 1] == '/') {
		uvalue_report_data_dir[strlen(uvalue_report_data_dir) - 1] = '/';
	}
	snprintf(uvalue_path_name, sizeof(uvalue_path_name) - 1, "%s/uvalue_report_%d_%d", 
				uvalue_report_data_dir, report_id, day_count);

	i_uvalue *p_uvalue = NULL;
	if (create_uvalue_instance(&p_uvalue) != 0) {
		fprintf(stderr, "ERROR: create_uvalue_instance");
		return -1;
	}
	if (p_uvalue->init(uvalue_path_name, i_uvalue::CREATE | i_uvalue::BTREE, 0600, NULL) != 0) {
		fprintf(stderr, "ERROR: p_uvalue->init(%s, i_uvalue::CREATE | i_uvalue::BTREE, 0600, NULL)", 
					uvalue_path_name);
		p_uvalue->release();
		return -1;

	}
	if (p_uvalue->set(&key, sizeof(key), &value, sizeof(value), NULL) != 0) {
		fprintf(stderr, "ERROR: p_uvalue->set");
		p_uvalue->uninit();
		p_uvalue->release();
		return -1;
	}
	if (p_uvalue->uninit() != 0) {
		fprintf(stderr, "ERROR: p_uvalue->uninit");
		return -1;
	}
	if (p_uvalue->release() != 0) {
		fprintf(stderr, "ERROR: p_uvalue->release");
		return -1;
	}

	return 0;
}

int uexpr(int argc, char **argv)
{
	if (argc != 2) {
		fprintf(stderr, "ERROR: parameter\nusage: uexpr \"[1, 0]\"\n");
		return -1;
	}

	i_uexpr *p_uexpr = NULL;
	if (create_uexpr_instance(&p_uexpr) != 0) {
		fprintf(stderr, "ERROR: create_uexpr_instance\n");
		return -1;
	}
	if (p_uexpr->init(g_p_config) != 0) {
		fprintf(stderr, "ERROR: p_uexpr->init\n");
		p_uexpr->release();
		return -1;

	}

	uexpr_so_result_t *p_uexpr_so_result = (uexpr_so_result_t *)p_uexpr->eval(argv[1]);
	if (p_uexpr_so_result == NULL) {
		fprintf(stderr, "ERROR: p_uexpr->eval(%s)\n", argv[1]);
		p_uexpr->uninit();
		p_uexpr->release();
		return -1;
	}

	DEBUG_LOG("SIZEOF(uexpr_so_result_t, result_len): %u", SIZEOF(uexpr_so_result_t, result_len));
	DEBUG_LOG("SIZEOF(uexpr_so_result_t, result_data[0]): %u", SIZEOF(uexpr_so_result_t, result_data[0]));

	for (uint32_t i = 0; i != ((p_uexpr_so_result->result_len - SIZEOF(uexpr_so_result_t, result_len)) / SIZEOF(uexpr_so_result_t, result_data[0])); ++i) {
		fprintf(stdout, "%-10u%u\n", i, p_uexpr_so_result->result_data[i]);
	}

	if (p_uexpr->uninit() != 0) {
		fprintf(stderr, "ERROR: p_uexpr->uninit\n");
		return -1;
	}
	if (p_uexpr->release() != 0) {
		fprintf(stderr, "ERROR: p_uexpr->release\n");
		return -1;
	}

	return 0;
}


/**
 * Slice a string up into argc/argv.
 */
static const char *alt_arg = NULL;               /* argv[1] with no shell-like preprocessing */
static int slr_flag = 0;
static char arg_buf[1024] = {0};                 /* argument storage buffer */
static const char *string_base = NULL;           /* current scan point in line buffer*/
static char *arg_base = NULL;                    /* current store point in arg buffer */

static char * slurp_string();

int make_argv(const char *line, char **pp_argv, int *p_argc)
{
	if (line == NULL || pp_argv == NULL || 
		p_argc == NULL || *p_argc <= 0) {
		return -1;
	}

	int max_argc = *p_argc;
	memset(arg_buf, 0, sizeof(arg_buf));
	*p_argc = 0;
	string_base = line;                        /* scan from first of buffer */
	arg_base = arg_buf;                          /* store from first of buffer */
	slr_flag = 0;

	while ((*pp_argv++ = slurp_string())) {
		++(*p_argc);
		if (*p_argc >= max_argc) {
			break;
		}
	}

	return 0;
}

/**
 * Parse string into argbuf;
 * implemented with FSM to
 * handle quoting and strings
 */
static char * slurp_string()
{
	int got_one = 0;
	const char *sb = string_base;
	char *ap = arg_base;
	char *tmp = arg_base;                        /* will return this if token found */

	if (*sb == '!' || *sb == '$') {              /* recognize ! as a token for shell and */
		switch (slr_flag) {                      /* $ as token for macro invoke */
			case 0:
				slr_flag++;
				string_base++;
				return ((*sb == '!') ? (char *)"!" : (char *)"$");

			case 1:
				slr_flag++;
				alt_arg = string_base;;
				break;

			default:
				break;
		}
	}

S0:
	switch (*sb) {
		case '\0':
			goto OUT;

		case ' ':
		case '\t':
			sb++;
			goto S0;

		default:
			switch (slr_flag) {
				case 0:
					slr_flag++;
					break;
					
				case 1:
					slr_flag++;
					alt_arg = sb;
					break;

				default:
					break;
			}
			goto S1;
	}
	
S1:
	switch (*sb) {
		case ' ':
		case '\t':
		case '\0':
			goto OUT;			                 /* end of token */

		case '\\':
			sb++;
			goto S2;			                 /* slurp next character */

		case '"':
			sb++;
			goto S3;			                 /* slurp quoted string */

		default:
			*ap++ = *sb++;		                 /* add character to token */
			got_one = 1;
			goto S1; }

S2:
	switch (*sb) {
		case '\0':
			goto OUT;

		default:
			*ap++ = *sb++;
			got_one = 1;
			goto S1;
	}

S3:
	switch (*sb) {
		case '\0':
			goto OUT;

		case '"':
			sb++;
			goto S1;

		default:
			*ap++ = *sb++;
			got_one = 1;
			goto S3;
	}

OUT:
	if (got_one) {
		*ap++ = '\0';
	}
	arg_base = ap;			                     /* update storage pointer */
	string_base = sb;		                     /* update scan pointer */
	if (got_one) {
		return (tmp);
	}
	switch (slr_flag) {
		case 0:
			slr_flag++;
			break;

		case 1:
			slr_flag++;
			alt_arg = (char *)0;
			break;

		default:
			break;
	}
	return ((char *)0);
}

