#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include "avl_tree.h"

#define MAX(a, b) (a > b ? a : b)
#define MIN(a, b) (a < b ? a : b)

#define HEIGHT(p) (p == NULL ? -1 : p->height)

int intval_compare(const void * a, const void * b)
{
    const int x = *((int *)a);
    const int y = *((int *)b);
    return x-y;
}

/////////////////////////////////////////////////////////////////////

// 右旋转 - 在P与它的左子树Q之间实施旋转，将左边的某树移到右边，如图，将Y移到右边：
/*
         P                   Q
        / \                 / \
       Q   z     ===>      x   P
      /  \                    / \
     x    y                  y   z
*/
avl_node * avl_tree::single_rotate_with_left(avl_node * p)
{
    avl_node * q = p->left;
    p->left = q->right;
    q->right = p;
    p->height = MAX(HEIGHT(p->left), HEIGHT(p->right)) + 1;
    q->height = MAX(HEIGHT(q->left), p->height) + 1;
    return q; // 返回新的root
}

// 左旋转， 与右旋转正好相反
avl_node * avl_tree::single_rotate_with_right(avl_node * p)
{
    avl_node * q = p->right;
    p->right = q->left;
    q->left = p;
    p->height = MAX(HEIGHT(p->left), HEIGHT(p->right)) + 1;
    q->height = MAX(p->height, HEIGHT(q->right)) + 1;
    return q;
}

// 左-右双旋转
/*
              P                            P                        Y
             / \                          / \                      / \
            X   d    X,Y右旋转           Y   d    P,Y左旋转       X   P
           / \      ==========>         / \      ==========>     / \ / \
          a   Y                        X   c                    a  b c  d
             / \                      / \
            b   c                    a   b
*/
avl_node * avl_tree::double_rotate_with_left(avl_node * p)
{
    p->left = single_rotate_with_right(p->left);
    return single_rotate_with_left(p);
}

// 右-左双旋转
avl_node * avl_tree::double_rotate_with_right(avl_node * p)
{
    p->right = single_rotate_with_left(p->right);
    return single_rotate_with_right(p);
}

// 搜索并删除右子树中最小的节点。
// d: 要删除的节点；t：递归用的树，初始值为d的右子树。
// 注意：此函数被调用时，d必有左、右两个儿子。
avl_node * avl_tree::delete_right_min(avl_node * d, avl_node * t)
{
    avl_node * temp;
    // 最小节点必定没有左儿子
    if(t->left == NULL) {
        temp = t;
        free(d->value);
        d->value = t->value;
        t = t->right;
        free(temp);
    } else {
        root->left = delete_right_min(d, root->left); // 很关键，且容易出错
    }
    return t;
}

avl_node * avl_tree::find(void * x, avl_node * t)
{
    if(t == NULL) {
        return NULL;
    }
    int r = compare(t->value, x);
    if(r > 0) {
        return find(x, t->left);
    } else if(r < 0) {
        return find(x, t->right);
    } else {// 递归终止条件
        return t;
    }
}

avl_node * avl_tree::insert(void * x, unsigned int len, avl_node * t)
{
    if(t == NULL) {
        t =(avl_node *)malloc(sizeof(avl_node));
        t->left = t->right = NULL;
        t->height = 0;
        t->value = (void *)malloc(len);
        memcpy(t->value, x, len);
    } else {
        int r = compare(x, t->value);
        if(r < 0) {
            t->left = insert(x, len, t->left);
            if(HEIGHT(t->left) - HEIGHT(t->right) == 2) {
                r = compare(x,  t->left->value) ;
                if(r < 0) {
                    t = single_rotate_with_left(t);
                } else {
                    t = double_rotate_with_left(t);
                }
            }
        } else if(r > 0) {
            t->right = insert(x, len, t->right);
            if(HEIGHT(t->right) - HEIGHT(t->left) == 2) {
                r = compare(x, t->right->value);
                if(r > 0) {
                    t = single_rotate_with_right(t);
                } else {
                    t = double_rotate_with_right(t);
                }
            }
        }
    }
    t->height = MAX(HEIGHT(t->left), HEIGHT(t->right)) + 1;
    return t;
}

avl_node * avl_tree::remove(void * x, avl_node * t)
{
    avl_node * temp;
    if(t == NULL) {
        return NULL;
    } else {
        int r = compare(x, t->value);
        if(r < 0) { // 从左子树删除，右子树可能过高
            t->left = remove(x, t->left);
            /*
               图示：在P的左子树删除
               (A) P的右子树的左子树高于P的右子树的右子树, 对P执行右-左双旋转
               P                  P                       Y
                \    先右旋        \        再左旋       / \
                 X   ======>        Y      =======>     P   X
                /                    \
               Y                      X
               (B) P的右子树的右子树高于P的右子树的左子树，对P进行左旋转
               P                           X
                \         左单旋转        / \
                 X       =========>      P   Y
                  \
                   Y
               */
            if(HEIGHT(t->right) - HEIGHT(t->left) == 2) {
                if(HEIGHT(t->right->left) > HEIGHT(t->right->right)) {
                    t = double_rotate_with_right(t);
                } else {
                    t = single_rotate_with_right(t);
                }
            }
        } else if(r > 0) {// 从右子树删除，左子树可能过高
            t->right = remove(x, t->right);
            if(HEIGHT(t->left) - HEIGHT(t->right) == 2) {
                if(HEIGHT(t->left->right) > HEIGHT(t->left->left)) {
                    t = double_rotate_with_left(t);
                } else {
                    t = single_rotate_with_left(t);
                }
            }
        } else if(t->left && t->right) {
            t->right = delete_right_min(t, t->right); // 找到右子树的最小节点，以其值替换当前值，并删除之。
            if(HEIGHT(t->left) - HEIGHT(t->right) == 2) {
                if(HEIGHT(t->left->right) > HEIGHT(t->left->left)) {
                    t = double_rotate_with_left(t);
                } else {
                    t = single_rotate_with_left(t);
                }
            }
        } else {
            temp = t;
            if(t->left == NULL) {
                t = t->right;
            } else if(t->right == NULL) {
                t = t->left;
            }
            free(temp->value);
            free(temp);
        }
    }
    if(t != NULL) {
        t->height = MAX(HEIGHT(t->left), HEIGHT(t->right)) + 1;
    }
    return t;
}

avl_node * avl_tree::make_empty(avl_node * p)
{
    if(p != NULL) {
        make_empty(p->left);
        make_empty(p->right);
        //printf("free %p\n", p);
        free(p->value);
        free(p);
    }
    return NULL;
}

void avl_tree::in_order(avl_node * p)
{
    if(order == NULL) {
        return ;
    }
    if(p != NULL) {
        in_order(p->left);
        //printf("(%p %p %p %u)\n", p->left, p, p->right, *((uint32_t *)(p->value)));
        order(p->value);
        in_order(p->right);
    }
}

//////////////////////////////////////////////////////////////////////////

avl_tree::avl_tree()
{
    compare = intval_compare;
    order = NULL;
    root = NULL; 
}

avl_tree::avl_tree(F_COMPARE fun_c, F_ORDER fun_o)
{
    compare = fun_c;
    order = fun_o;
    root = NULL;
}

avl_tree::~avl_tree()
{
    root = make_empty(root);
}

void avl_tree::set_compare(F_COMPARE fun)
{
    compare = fun;
}

void avl_tree::set_order(F_ORDER fun)
{
    order = fun;
}

// make_empty()与find()系列函数都与普通二叉查找树相同。
int avl_tree::make_empty()
{
    root = make_empty(root);
    return 0;
}

const void * avl_tree::find(void * x)
{
    avl_node * p = find(x, root);
    if(p != NULL) {
        return p->value;
    } else {
        return NULL;
    }
}

const void * avl_tree::min()
{
    if(root != NULL) {
        while(root->left != NULL) {
            root = root->left;
        }
        return root->value;
    } else {
        return NULL;
    }    
}

const void * avl_tree::max()
{
    if(root != NULL) {
        while(root->right != NULL) {
            root = root->right;
        }
        return root->value;
    } else {
        return NULL;
    }    
}

int avl_tree::insert(void * x, unsigned int len)
{
    root = insert(x, len, root);
    return root == NULL ? -1 : 0;
}

int avl_tree::remove(void * x)
{
    root = remove(x, root);
    return root == NULL ? -1 : 0;
}

void avl_tree::in_order()
{
    if(order == NULL) {
        return ;
    }
    if(root != NULL) {
        in_order(root->left);
        //printf("(%p %p %p %u)\n", root->left, root, root->right, *((uint32_t *)(root->value)));
        order(root->value);
        in_order(root->right);
        //printf("\nroot = %p\n", root);
    }
}
