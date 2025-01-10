package com.example.jetnews.glance.ui.layout

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.Action
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.jetnews.R
import com.example.jetnews.data.posts.impl.posts
import com.example.jetnews.glance.ui.layout.ImageGridLayoutDimensions.contentPadding
import com.example.jetnews.glance.ui.layout.ImageGridLayoutDimensions.gridCells
import com.example.jetnews.glance.ui.layout.ImageGridLayoutDimensions.imageCornerRadius
import com.example.jetnews.glance.ui.layout.ImageGridLayoutDimensions.itemCornerRadius
import com.example.jetnews.glance.ui.layout.ImageGridLayoutDimensions.textStartMargin
import com.example.jetnews.glance.ui.layout.ImageGridLayoutDimensions.titleTextBreakpoint
import com.example.jetnews.glance.ui.layout.ImageGridLayoutSize.Large
import com.example.jetnews.glance.ui.layout.ImageGridLayoutSize.Medium
import com.example.jetnews.glance.ui.layout.ImageGridLayoutSize.Small
import com.example.jetnews.glance.ui.layout.ImageGridLayoutSize.XSmall
import com.example.jetnews.ui.MainActivity

/**
 * A layout focused on presenting a grid of images (with optional title and supporting text). The
 * list is displayed in a [Scaffold] below an app-specific title bar.
 *
 * In this sample layout, image is the primary focus of the widget and title & supporting text act
 * as secondary information.
 *
 * The layout scales by adjusting the number of grid cells, and the font size of the texts (if
 * present).
 *
 * [com.example.layoutsamples.collections.ImageGridAppWidget] loads the images as bitmaps, and
 * scales them to fit into widget's limits. Number of items are capped. When using bitmaps, this
 * approach is suitable for use cases that recommend / feature limited items.
 *
 * The layout serves as an implementation suggestion, but should be customized to fit your
 * product's needs.
 *
 * @param title the text to be displayed as title of the widget, e.g. name of your widget or app.
 * @param titleIconRes a tintable icon that represents your app or brand, that can be displayed
 * with the provided [title]. In this sample, we use icon from a drawable resource, but you should
 * use an appropriate icon source for your use case.
 * @param titleBarActionIconRes resource id of a tintable icon that can be displayed as
 * an icon button within the title bar area of the widget. For example, a search icon.
 * @param titleBarActionIconContentDescription description of the [titleBarActionIconRes] button
 * to be used by the accessibility services.
 * @param titleBarAction action to be performed on click of the [titleBarActionIconRes] button.
 * @param items list of items to be displayed in the grid; typically, image with optional title
 *              and supporting text.
 *
 * @see [ImageGridItemData] for accepted inputs.
 * @see [com.example.layoutsamples.collections.ImageGridAppWidget]
 */
@Composable
fun ImageGridLayout(
  title: String,
  @DrawableRes titleIconRes: Int,
  @DrawableRes titleBarActionIconRes: Int,
  titleBarActionIconContentDescription: String,
  titleBarAction: () -> Unit,
  items: List<ImageGridItemData>,
) {

  @Composable
  fun TitleBar() {
    TitleBar(
      startIcon = ImageProvider(titleIconRes),
      title = title.takeIf { LocalSize.current.width >= titleTextBreakpoint } ?: "",
      iconColor = GlanceTheme.colors.primary,
      textColor = GlanceTheme.colors.onSurface,
      actions = {
        CircleIconButton(
          imageProvider = ImageProvider(titleBarActionIconRes),
          contentDescription = titleBarActionIconContentDescription,
          contentColor = GlanceTheme.colors.secondary,
          backgroundColor = null, // transparent
          onClick = titleBarAction
        )
      }
    )
  }

  Scaffold(
    titleBar = { TitleBar() },
    backgroundColor = GlanceTheme.colors.widgetBackground,
    horizontalPadding = contentPadding,
    modifier = GlanceModifier.padding(bottom = contentPadding)
  ) {
    if (items.isEmpty()) {
      EmptyListContent()
    } else {
      when(ImageGridLayoutSize.fromLocalSize()) {
        XSmall -> ListView(
          items = items,
          displayImage = false,
          displayTrailingIconIfPresent = false
        )

        Small -> ListView(
          items = items,
          displayImage = true,
          displayTrailingIconIfPresent = shouldDisplayTrailingIconButton()
        )

        else -> Grid(items)
      }
    }
  }
}

@Composable
private fun Grid(items: List<ImageGridItemData>) {
  RoundedScrollingLazyVerticalGrid(
    modifier = GlanceModifier.fillMaxSize(),
    gridCells = gridCells,
    items = items,
    itemContentProvider = { item ->
      GridItem(
        item = item,
        modifier = GlanceModifier.fillMaxSize()
      )
    })
}

@Composable
private fun GridItem(
  item: ImageGridItemData,
  modifier: GlanceModifier,
) {
  @Composable
  fun Image() {
    Image(
      provider = ImageProvider(item.image),
      contentDescription = item.imageContentDescription,
      contentScale = ContentScale.Fit,
      modifier = GlanceModifier
        .cornerRadius(imageCornerRadius)
        .fillMaxWidth()
        .wrapContentHeight()
    )
  }

  @Composable
  fun Title(text: String) {
    Text(
      text = text,
      maxLines = 2,
      style = ImageGridLayoutTextStyles.titleText,
      modifier = GlanceModifier.padding(start = textStartMargin)
    )
  }

  @Composable
  fun SupportingText(text: String) {
    Text(
      text = text,
      maxLines = 2,
      style = ImageGridLayoutTextStyles.supportingText,
      modifier = GlanceModifier.padding(start = textStartMargin)
    )
  }

  if (item.title != null) {
    VerticalListItem(
      modifier = modifier
        .cornerRadius(itemCornerRadius)
        .clickable {
          //TODO: add click
        },
      topContent = { Image() },
      titleContent = { Title(text = item.title) },
      supportingContent = takeComposableIf(item.supportingText != null) {
        SupportingText(text = checkNotNull(item.supportingText))
      },
      trailingBottomContent = {
        CircleIconButton(
          // TODO: use filled/ non-filled button.
          imageProvider = ImageProvider(R.drawable.ic_jetnews_bookmark),
          contentDescription = "bookmark",
          backgroundColor = null, // transparent
          onClick = {}
        )
      }
    )
  } else {
    Box(modifier) {
      Image()
    }
  }
}

/** Returns the provided [block] composable if [predicate] is true, else returns null */
@Composable
private inline fun takeComposableIf(
  predicate: Boolean,
  crossinline block: @Composable () -> Unit,
): (@Composable () -> Unit)? {
  return if (predicate) {
    { block() }
  } else null
}

data class ImageGridItemData(
  val key: String,
  val image: Int,
  val imageContentDescription: String?,
  val title: String? = null,
  val supportingText: String? = null,
)

/**
 * Size of the widget per the reference breakpoints. Each size has its own display
 * characteristics such as - number of grid cells, font sizes, etc.
 *
 * In this layout, only width breakpoints are used to scale the layout.
 */
private enum class ImageGridLayoutSize(val maxWidth: Dp) {
  // Single column vertical list without images or trailing button in this size.
  XSmall(maxWidth = 260.dp),

  // Single column horizontal list with images and optional trailing button if exists.
  Small(maxWidth = 479.dp),

  // Larger fonts, 2 column
  Medium(maxWidth = 519.dp),

  // 3 column
  Large(maxWidth = Dp.Infinity);

  companion object {
    /**
     * Returns the corresponding [ImageGridLayoutSize] to be considered for the current widget
     * size.
     */
    @Composable
    fun fromLocalSize(): ImageGridLayoutSize {
      val size = LocalSize.current

      ImageGridLayoutSize.values().forEach {
        if (size.width < it.maxWidth) {
          return it
        }
      }
      throw IllegalStateException("No mapped size ")
    }
  }
}

private object ImageGridLayoutTextStyles {
  /**
   * Style for the text displayed as title within each item.
   */
  val titleText: TextStyle
    @Composable get() = TextStyle(
      fontWeight = FontWeight.Medium,
      fontSize = if (ImageGridLayoutSize.fromLocalSize() == Small) {
        14.sp // M3 Title Small
      } else {
        16.sp // M3 Title Medium
      },
      color = GlanceTheme.colors.onSurface
    )

  /**
   * Style for the text displayed as supporting text within each item.
   */
  val supportingText: TextStyle
    @Composable get() =
      TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp, // M3 Label Medium
        color = GlanceTheme.colors.secondary
      )
}


/**
 * A vertical scrolling list displaying [FilledHorizontalListItem]s. Suitable for
 * [ImageTextListLayoutSize.Small] and [ImageTextListLayoutSize.Large] sizes.
 */
@Composable
private fun ListView(
  items: List<ImageGridItemData>,
  displayImage: Boolean,
  displayTrailingIconIfPresent: Boolean,
) {
  RoundedScrollingLazyColumn(
    modifier = GlanceModifier.fillMaxSize(),
    items = items,
    verticalItemsSpacing = 4.dp,
    itemContentProvider = { item ->
      FilledHorizontalListItem(
        item = item,
        displayImage = displayImage,
        displayTrailingIcon = displayTrailingIconIfPresent,
        onClick = actionStartActivity<MainActivity>(),
        modifier = GlanceModifier.fillMaxWidth(),
      )
    },
  )
}

/**
 * Arranges the texts, the image and the icon button in a horizontal arrangement with a filled
 * container.
 */
@Composable
private fun FilledHorizontalListItem(
  item: ImageGridItemData,
  displayImage: Boolean,
  displayTrailingIcon: Boolean,
  onClick: Action,
  modifier: GlanceModifier = GlanceModifier,
) {
  @Composable
  fun TitleText() {
    Text(
      text = item.title ?: "placeholder title",
      maxLines = 2,
      style = titleText,
    )
  }

  @Composable
  fun SupportingText() {
    Text(
      text = item.supportingText ?: "placeholder subtitle",
      maxLines = 2,
      style = supportingText
    )
  }

  @Composable
  fun SupportingImage() {
    Image(
      provider = ImageProvider(item.image),
      // contentDescription is null because in this sample, it serves merely as a visual; but if
      // it gives additional info to user, you should set the appropriate content description.
      contentDescription = null,
      // Depending on your image content, you may want to select an appropriate ContentScale.
      contentScale = ContentScale.Crop,
      // Fixed size per UX spec
      modifier = modifier.cornerRadius(imageCornerRadius).size(68.dp)
    )
  }

  @Composable
  fun IconButton() {
    // Using CircleIconButton allows us to keep the touch target 48x48
    CircleIconButton(
      imageProvider = ImageProvider(R.drawable.ic_jetnews_bookmark),
      backgroundColor = null, // to show transparent background.
      contentDescription = "bookmark",
      onClick = actionStartActivity<MainActivity>()
    )
  }

  ListItem(
    modifier = modifier
      .padding(12.dp)
      .cornerRadius(16.dp)
      .background(GlanceTheme.colors.secondaryContainer),
    headlineContent = { TitleText() },
    supportingContent = { SupportingText() },
    onClick = onClick,
    leadingContent = if (displayImage) {
      { SupportingImage() }
    } else {
      null
    },
    trailingContent = if (displayTrailingIcon) {
      { IconButton() }
    } else null,
  )
}

/**
 * Style for the text displayed as title within each item.
 */
val titleText: TextStyle
  @Composable get() = TextStyle(
    fontWeight = FontWeight.Medium,
    fontSize = if (ImageGridLayoutSize.fromLocalSize() == Small) {
      14.sp // M3 Title Small
    } else {
      16.sp // M3 Title Medium
    },
    color = GlanceTheme.colors.onSurface
  )

/**
 * Style for the text displayed as supporting text within each item.
 */
val supportingText: TextStyle
  @Composable get() =
    TextStyle(
      fontWeight = FontWeight.Normal,
      fontSize = 12.sp, // M3 Label Medium
      color = GlanceTheme.colors.secondary
    )

private object ImageGridLayoutDimensions {
  val contentPadding = 12.dp

  val titleTextBreakpoint = 200.dp

  /**
   * Amount of space before the text in each item
   */
  val textStartMargin = 4.dp

  /** Corner radius for image in each item. */
  val imageCornerRadius = 16.dp

  /** Corner radius applied to each item **/
  val itemCornerRadius = 16.dp

  /**
   * Number of columns the grid layout should use to display items in available space.
   */
  val gridCells: Int
    @Composable get() {
      return when (ImageGridLayoutSize.fromLocalSize()) {
        Medium -> 2
        Large -> 3
        else -> 1
      }
    }
}

/**
 * Returns if icon button should be displayed across medium and large sizes based on
 * predefined breakpoints.
 */
@Composable
fun shouldDisplayTrailingIconButton(): Boolean {
  val widgetWidth = LocalSize.current.width
  return (widgetWidth in 340.dp..479.dp || widgetWidth > 620.dp)
}

/**
 * Preview sizes for the widget covering the width based breakpoints of the image grid layout.
 *
 * This allows verifying updates across multiple breakpoints.
 */
@OptIn(ExperimentalGlancePreviewApi::class)
@Preview(widthDp = 220, heightDp = 350)
@Preview(widthDp = 260, heightDp = 350)
@Preview(widthDp = 350, heightDp = 350)
@Preview(widthDp = 480, heightDp = 350)
@Preview(widthDp = 321, heightDp = 350)
@Preview(widthDp = 518, heightDp = 350)
@Preview(widthDp = 520, heightDp = 350)
private annotation class ImageGridBreakpointPreviews

/**
 * Previews for the image grid layout with both title and supporting text below the image
 *
 * First we look at the previews at defined breakpoints, tweaking them as necessary. In addition,
 * the previews at standard sizes allows us to quickly verify updates across min / max and common
 * widget sizes without needing to run the app or manually place the widget.
 */
@ImageGridBreakpointPreviews
@Composable
private fun ImageTextGridLayoutPreview() {
  val context = LocalContext.current

  ImageGridLayout(
    title = context.getString(R.string.app_name),
    titleIconRes = R.drawable.ic_jetnews_logo,
    titleBarActionIconRes = R.drawable.ic_jetnews_search,
    titleBarActionIconContentDescription = context.getString(
      R.string.cd_search
    ),
    titleBarAction = {},
    items = posts.allPosts.map { post ->
      ImageGridItemData(
        key = post.id,
        image = post.imageThumbId,
        imageContentDescription = null,
        title = post.title,
        supportingText = post.subtitle
      )
    }
  )
}