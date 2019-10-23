/**
 * =====================================================================================
 *       @file  avl_tree.h
 *      @brief  参考 http://blog.csdn.net/blackboyofsnp/article/details/6324229
 *
 *     Created  2012-03-13 10:25:10
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */


#ifndef __AVL_TREE_H__
#define __AVL_TREE_H__
//////////////////////////////////////////////////////////////////////////

struct _avlnode;
typedef struct _avlnode avl_node;

struct _avlnode {
    void * value;
    avl_node * left;
    avl_node * right;
    int height;
};

typedef int (*F_COMPARE)(const void *, const void *);
typedef void (*F_ORDER)(const void *);

class avl_tree
{
private :
    avl_node * root;
    F_COMPARE compare;
    F_ORDER   order;

public :
    avl_tree();
    ~avl_tree();
    avl_tree(F_COMPARE, F_ORDER = 0);
    void set_compare(F_COMPARE);
    void set_order(F_ORDER);
    int make_empty();
    const void * find(void *);
    const void * min();
    const void * max();
    int  insert(void *, unsigned int);
    int  remove(void *);
    void in_order();

private :
    avl_node * single_rotate_with_left(avl_node *);
    avl_node * single_rotate_with_right(avl_node *);
    avl_node * double_rotate_with_left(avl_node *);
    avl_node * double_rotate_with_right(avl_node *);
    avl_node * delete_right_min(avl_node *, avl_node *);
    avl_node * find(void *, avl_node *);
    avl_node * insert(void *, unsigned int, avl_node *);
    avl_node * remove(void *, avl_node *);
    avl_node * make_empty(avl_node *);
    void in_order(avl_node *);
};

#endif
