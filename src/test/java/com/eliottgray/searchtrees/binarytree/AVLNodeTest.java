package com.eliottgray.searchtrees.binarytree;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class AVLNodeTest {
    private Integer zero = 0;
    private Integer one = 1;
    private Integer two = 2;
    private Integer three = 3;
    private Integer four = 4;
    private Integer five = 5;
    private Integer six = 6;
    private Comparator<Integer> comparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer one, Integer two) {
            return one.compareTo(two);
        }
    };

    /**
     * Construct a single node, and test properties.
     */
    @Test
    public void testSingleNode() throws InvalidSearchTreeException {
        AVLNode<Integer> root = new AVLNode<>(zero);
        assertFalse(root.hasLeft());
        assertFalse(root.hasRight());
        assertNull(root.left);
        assertNull(root.right);
        assertEquals(root.key, new Integer(0));
        assertEquals(1, root.size);
        assertEquals(1, root.height);
        assertTrue(root.contains(root.key, comparator));
        root.validate();
    }

    /**
     * Manually construct a small graph of nodes, and test properties.
     *
     * Ensure that upon creation of a new tree, that old tree retains its identities and properties.
     *
     *          3
     *         /
     *       1
     */
    @Test
    public void testInsertions_testImmutability() throws InvalidSearchTreeException {
        // Start graph with initial node.
        AVLNode<Integer> root = new AVLNode<>(three);
        root.validate();

        // Create new tree with additional node.
        AVLNode<Integer> newRoot = root.insert(one, comparator);
        newRoot.validate();

        // Verify old tree is unchanged.
        assertEquals(three, root.key);
        assertFalse(root.hasLeft());
        assertFalse(root.hasRight());
        assertEquals(1, root.size);
        assertEquals(1, root.height);
        assertFalse(root.contains(one, comparator));
        assertTrue(root.contains(three, comparator));

        // Verify new tree contains new information.
        assertEquals(three, newRoot.key);
        assertTrue(newRoot.hasLeft());
        assertFalse(newRoot.hasRight());
        assertEquals(one, newRoot.left.key);
        assertFalse(newRoot.left.hasLeft() && newRoot.left.hasRight());
        assertEquals(2, newRoot.size);
        assertEquals(2, newRoot.height);
        assertTrue(newRoot.contains(newRoot.left.key, comparator));
        assertTrue(newRoot.contains(newRoot.key, comparator));
    }

    /**
     * Test that adding a duplicate node results in replacement of that node with the new one.
     */
    @Test
    public void testInsertions_withDuplicateKeys(){

        // Establish initial graph.
        AVLNode<Integer> root = new AVLNode<>(five);
        root = root.insert(one, comparator);

        // Add duplicate root node.
        AVLNode<Integer> rootTwo = root.insert(five, comparator);

        // Test that root nodes have a different identity, but they share the same child node.
        assertEquals(root.key, rootTwo.key);
        assertNotEquals(root, rootTwo);
        assertEquals(root.left, rootTwo.left);

        // Add duplicate child node.
        AVLNode<Integer> rootThree = rootTwo.insert(one, comparator);

        // Test that both root and child nodes of newest tree have new identities; replacing child requires replacing parent.
        assertEquals(rootTwo.key, rootThree.key);
        assertNotEquals(rootTwo, rootThree);
        assertEquals(rootTwo.left.key, rootThree.left.key);
        assertNotEquals(rootTwo.left, rootThree.left);
    }

    /**
     * Add multiple nodes to the far right branch, until the tree rotates 3 times.
     *
     *  { Simple Rotation }        { Simple Rotation }              { Complex Rotation }
     *
     *         Left        Insert          Left        Insert         Right        Left
     *    1            2           2               2             2          2             3
     *     \    ->    / \    ->   / \      ->     / \    ->     / \   ->   / \     ->    / \
     *      2        1   3       1   3           1   5         1   5      1   3         2   5
     *       \                        \             / \           / \          \       /   / \
     *        3                        5           3   6         3   6          5     1   4   6
     *                                  \                         \            / \
     *                                   6                         4          4   6
     */
    @Test
    public void testAddValues_repeatedRotations_left() throws InvalidSearchTreeException {
        AVLNode<Integer> root = new AVLNode<>(one);

        // Unbalance tree with insertions.
        root = root.insert(two, comparator);
        root = root.insert(three, comparator);

        // Verify first rotation
        assertEquals(two, root.key);
        assertEquals(one, root.left.key);
        assertEquals(three, root.right.key);
        assertEquals(3, root.size);
        assertEquals(2, root.height);
        root.validate();

        // Unbalance tree with insertions.
        root = root.insert(five, comparator);
        root = root.insert(six, comparator);

        // Verify second rotation
        assertEquals(two, root.key);
        assertEquals(one, root.left.key);
        assertEquals(five, root.right.key);
        assertEquals(three, root.right.left.key);
        assertEquals(six, root.right.right.key);
        assertEquals(5, root.size);
        assertEquals(3, root.height);
        root.validate();

        // Unbalance tree with insertions.
        root = root.insert(four, comparator);

        // Verify complex rotation.
        assertEquals(three, root.key);
        assertEquals(two, root.left.key);
        assertEquals(one, root.left.left.key);
        assertEquals(five, root.right.key);
        assertEquals(four, root.right.left.key);
        assertEquals(six, root.right.right.key);
        assertEquals(6, root.size);
        assertEquals(3, root.height);
        root.validate();

    }

    /**
     * Add multiple nodes to the far left branch, until the tree rotates 3 times.
     *
     *  { Simple Rotation }        { Simple Rotation }              { Complex Rotation }
     *
     *         Right       Insert          Right        Insert         Left        Right
     *       6         5            5               5             5          5             4
     *      /   ->    / \    ->    / \      ->     / \    ->     / \   ->   / \     ->    / \
     *     5         4   6        4   6           2   6         2   6      4   6         2   5
     *    /                      /               / \           / \        /             / \   \
     *   4                      2               1   4         1   4      2             1   3   6
     *                         /                                 /      / \
     *                        1                                 3      1   3
     */
    @Test
    public void testAddValues_repeatedRotations_right() throws InvalidSearchTreeException {
        AVLNode<Integer> root = new AVLNode<>(six);

        // Unbalance tree with insertions.
        root = root.insert(five, comparator);
        root = root.insert(four, comparator);

        // Verify first rotation
        assertEquals(five, root.key);
        assertEquals(six, root.right.key);
        assertEquals(four, root.left.key);
        assertEquals(3, root.size);
        assertEquals(2, root.height);
        root.validate();

        // Unbalance tree with insertions.
        root = root.insert(two, comparator);
        root = root.insert(one, comparator);

        // Verify second rotation
        assertEquals(five, root.key);
        assertEquals(two, root.left.key);
        assertEquals(six, root.right.key);
        assertEquals(one, root.left.left.key);
        assertEquals(four, root.left.right.key);
        assertEquals(5, root.size);
        assertEquals(3, root.height);
        root.validate();

        // Unbalance tree with insertions.
        root = root.insert(three, comparator);

        // Verify complex rotation.
        assertEquals(four, root.key);
        assertEquals(two, root.left.key);
        assertEquals(five, root.right.key);
        assertEquals(six, root.right.right.key);
        assertEquals(one, root.left.left.key);
        assertEquals(three, root.left.right.key);
        assertEquals(6, root.size);
        assertEquals(3, root.height);
        root.validate();

    }

    /**
     * Test deletion of multiple nodes, with varying number of children.
     *
     * Nodes to be deleted in brackets: []
     *
     *              Delete        Delete       Delete     Delete    Delete
     *        4              4            4           [4]      [3]
     *       / \            / \          / \          /
     *      2   5    ->   [2]  5   ->   3  [5]  ->   3     ->       ->   [empty]
     *       \   \          \
     *        3  [6]         3
     */
    @Test
    public void testDelete_zeroOrOneChild() throws InvalidSearchTreeException {
        // Construct initial tree.
        AVLNode<Integer> root = new AVLNode<>(four);
        root = root.insert(two, comparator);
        root = root.insert(five, comparator);
        root = root.insert(three, comparator);
        root = root.insert(six, comparator);
        root.validate();

        // Test deletion of a node with no children.
        root = root.delete(six, comparator);
        assertEquals(four, root.key);
        assertEquals(five, root.right.key);
        assertFalse(root.right.hasRight());
        assertEquals(1, root.right.size);
        assertEquals(1, root.right.height);
        assertEquals(3, root.height);
        assertEquals(4, root.size);
        root.validate();

        // Test deletion of a node with one child.
        root = root.delete(two, comparator);
        assertEquals(four, root.key);
        assertEquals(three, root.left.key);
        assertEquals(five, root.right.key);
        assertEquals(2, root.height);
        assertEquals(3, root.size);
        assertEquals(1, root.left.height);
        root.validate();

        // Test deletion of a node with no children.
        root = root.delete(five, comparator);
        assertEquals(four, root.key);
        assertNull(root.right);
        assertEquals(2, root.height);
        assertEquals(2, root.size);
        root.validate();

        // Test deletion of Root with one child.
        root = root.delete(four, comparator);
        assertEquals(three, root.key);
        assertFalse(root.hasLeft() && root.hasRight());
        assertEquals(1, root.size);
        assertEquals(1, root.height);
        root.validate();

        // Test deletion of Root with no child.
        root = root.delete(3, comparator);
        assertNull(root);
    }
//
    /**
     * Test deletion which causes right rotation of tree.
     *
     *             Delete     Rotate
     *        3            3         2
     *       / \          /         / \
     *      2  [4]  ->   2    ->   1   3
     *     /            /
     *    1            1
     */
    @Test
    public void testDelete_rightRotation() throws InvalidSearchTreeException {
        AVLNode<Integer> root = new AVLNode<>(three);
        root = root.insert(four, comparator);
        root = root.insert(two, comparator);
        root = root.insert(one, comparator);

        assertEquals(three, root.key);

        // Deletion should cause rotation.
        root = root.delete(four, comparator);
        assertEquals(two, root.key);
        assertEquals(one, root.left.key);
        assertEquals(three, root.right.key);
        assertEquals(2, root.height);
        assertEquals(3, root.size);
        root.validate();

    }

    /**
     * Test deletion which causes left rotation of tree.
     *
     *           Delete      Rotate
     *        3          3            4
     *       / \          \          / \
     *     [2]  4    ->    4   ->   3   5
     *           \          \
     *            5          5
     */
    @Test
    public void testDelete_leftRotation() throws InvalidSearchTreeException {
        AVLNode<Integer> root = new AVLNode<>(three);
        root = root.insert(four, comparator);
        root = root.insert(two, comparator);
        root = root.insert(five, comparator);

        assertEquals(three, root.key);

        // Deletion should cause rotation.
        root = root.delete(two, comparator);
        assertEquals(four, root.key);
        assertEquals(three, root.left.key);
        assertEquals(five, root.right.key);
        assertEquals(2, root.height);
        assertEquals(3, root.size);
        root.validate();

    }

    /**
     * Test deletion of nodes with two children
     *
     *              Delete        Delete        Delete
     *        [4]             [3]             5             5
     *       /   \           /   \           / \           / \
     *     1      6    ->   1     6    ->  [1]  6    ->   2   6
     *    / \    /         / \    /        / \           /
     *   0  (3) 5         0   2 (5)       0  (2)        0
     *      /
     *     2
     */
    @Test
    public void testDelete_nodeWithTwoChildren() throws InvalidSearchTreeException {
        AVLNode<Integer> root = new AVLNode<>(four);
        root = root.insert(one, comparator);
        root = root.insert(six, comparator);
        root = root.insert(three, comparator);
        root = root.insert(zero, comparator);
        root = root.insert(five, comparator);
        root = root.insert(two, comparator);

        assertEquals(four, root.key);
        assertEquals(4, root.height);
        root.validate();

        // Test deletion of root when LEFT subtree is longer.
        root = root.delete(4, comparator);
        assertEquals(three, root.key);
        assertEquals(one, root.left.key);
        assertEquals(six, root.right.key);
        assertEquals(five, root.right.left.key);
        assertEquals(zero, root.left.left.key);
        assertEquals(two, root.left.right.key);
        assertEquals(3, root.height);
        assertEquals(6, root.size);
        root.validate();

        // Test deletion of root when Right subtree is longer OR subtrees are same size.
        root = root.delete(3, comparator);
        assertEquals(five, root.key);
        assertEquals(one, root.left.key);
        assertEquals(six, root.right.key);
        assertEquals(zero, root.left.left.key);
        assertEquals(two, root.left.right.key);
        assertEquals(3, root.height);
        assertEquals(5, root.size);
        root.validate();

        // Test deletion of non-root with two children.
        root = root.delete(one, comparator);
        assertEquals(five, root.key);
        assertEquals(two, root.left.key);
        assertEquals(six, root.right.key);
        assertEquals(zero, root.left.left.key);
        assertEquals(3, root.height);
        assertEquals(4, root.size);
        root.validate();
    }

    /**
     * Attempt to delete keys which are not in the tree.
     */
    @Test
    public void testDelete_whenNothingToDelete() throws InvalidSearchTreeException {
        AVLNode<Integer> root = new AVLNode<>(four);
        root = root.insert(one, comparator);

        // Delete value that isn't in the tree, larger than largest node.
        root = root.delete(99, comparator);

        // Verify tree is unchanged.
        assertEquals(root.key, four);
        assertEquals(one, root.left.key);
        assertEquals(2, root.height);
        assertEquals(2, root.size);
        root.validate();

        // Delete value that isn't in the tree, smaller than smallest node.
        root = root.delete(-199, comparator);

        // Verify tree is unchanged.
        assertEquals(root.key, four);
        assertEquals(one, root.left.key);
        assertEquals(2, root.height);
        assertEquals(2, root.size);
        root.validate();

        // Delete value that isn't in the tree, between the available node values.
        root = root.delete(3, comparator);
        // Verify tree is unchanged.
        // Verify same results as before the deletion.
        assertEquals(root.key, four);
        assertEquals(one, root.left.key);
        assertEquals(2, root.height);
        assertEquals(2, root.size);
        root.validate();
    }

    /**
     * Test that deletion of nodes properly results in replacement of all direct parent nodes.
     */
    @Test
    public void testDelete_maintainsImmutability() {
        AVLNode<Integer> root = new AVLNode<>(four);
        root = root.insert(one, comparator);
        root = root.insert(six, comparator);

        // Delete left child of root, leaving root and right child.
        AVLNode postDelete = root.delete(one, comparator);

        // Ensure that root now has a new identity, but right child remains the same.
        assertEquals(root.key, postDelete.key);
        assertNotEquals(root, postDelete);
        assertEquals(root.right, postDelete.right);
    }

    /**
     * Test ability to search tree for specific Key.
     */
    @Test
    public void testContains(){
        AVLNode<Integer> root = new AVLNode<>(five);
        root = root.insert(four, comparator);
        root = root.insert(six, comparator);

        // Test if root is found.
        assertTrue(root.contains(five, comparator));

        // Test if left leaf is found.
        assertTrue(root.contains(four, comparator));

        // Test if right leaf is found.
        assertTrue(root.contains(six, comparator));

        // Test if unavailable node is reported as unavailable.
        assertFalse(root.contains(zero, comparator));
    }

    /**
     * Test traversal to in-order sorted list.
     */
    @Test
    public void testInOrderTraversal() throws InvalidSearchTreeException {
        AVLNode<Integer> root = new AVLNode<>(five);
        root = root.insert(six, comparator);
        root = root.insert(zero, comparator);
        root = root.insert(one, comparator);
        root = root.insert(two, comparator);
        root = root.insert(three, comparator);

        // Test in order.
        List<Integer> expectedList = Arrays.asList(zero, one, two, three, five, six);
        List<Integer> actualInOrderList = root.inOrderTraversal();
        assertEquals(expectedList, actualInOrderList);

        root.validate();
    }

    /**
     * Test correct retrieval of a subset of Values based on a range of Keys.
     */
    @Test
    public void testGetRangeList_presentInTree(){
        AVLNode<Integer> root = new AVLNode<>(one);

        root = root.insert(two, comparator);
        root = root.insert(three, comparator);
        root = root.insert(four, comparator);
        root = root.insert(five, comparator);

        List<Integer> expectedValues = new ArrayList<Integer>(){{add(two); add(three); add(four);}};
        List<Integer> actualValues = root.getRange(two, four, comparator);
        assertEquals(expectedValues, actualValues);
    }

    /**
     * Test correct retrieval of a subset of Values based on a range of Keys.
     */
    @Test
    public void testGetRangeList_notPresentInTree(){
        AVLNode<Integer> root = new AVLNode<>(one);

        List<Integer> expectedValues = new ArrayList<>();
        List<Integer> actualValues = root.getRange(two, four, comparator);
        assertEquals(expectedValues, actualValues);
    }
}
