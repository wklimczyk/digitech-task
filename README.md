Recruitment task, which was not fully implemented, mostly due to the lack of time.

1. LayoutManager for RecyclerView, which:
• Populates items in a grid horizontally, like:
Item1 Item2 Item3 Item4 Item5 | Item11 Item12 Item13 Item14 Item15
Item6 Item7 Item8 Item9 Item10 | Item16 Item17 ...
• Number of rows and columns are configurable in the constructor (in the case
above: 2 rows, 5 columns)
• supports reversed (right to left) layout
• supports RecyclerView item animations
• supports smooth scrolling for changing page index in code
2. SnapHelper for RecyclerView and beforementioned LayoutManager,
which:
• supports reversed (right to left) layout
• when attached to RecyclerView, forces the RecyclerView to always scroll whole
pages
• one "whole" page is a specific number of items, which is calculated by multiplying
rows * cols
• if rows = 2 and cols = 5, then the RecyclerView should always scroll 10 items and
align the 10th+1 item at the starting edge of the screen,
• Supports drag & drop (2 recycler views)
