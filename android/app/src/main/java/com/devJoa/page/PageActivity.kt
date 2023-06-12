package com.devJoa.page

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.devJoa.LoadingDialog
import com.devJoa.MainActivity
import com.devJoa.R
import com.devJoa.config.ApplicationClass
import com.devJoa.databinding.ActivityPageBinding
import com.devJoa.dto.User
import com.devJoa.page.home.HomeFragment
import com.devJoa.page.my.EditMyPageDialog
import com.devJoa.page.my.EditProfileDialog
import com.devJoa.page.my.MyPageFragment
import com.devJoa.page.post.PostDetailFragment
import com.devJoa.page.post.PostFragment
import com.devJoa.page.product.ProductFragment
import com.devJoa.page.search.SearchPostFragment
import com.devJoa.page.search.SearchUserFragment
import com.devJoa.util.RetrofitUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "PageActivity_싸피"

class PageActivity : AppCompatActivity(), EditMyPageDialog.EditMyPageDialogListener, EditProfileDialog.EditProfileListener {
    lateinit var binding: ActivityPageBinding
    lateinit var loadingDialog: com.devJoa.LoadingDialog
    private val pageViewModel: PageViewModel by viewModels()
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = ApplicationClass.sharedPreferencesUtil.getUser()
        pageViewModel.setUser(user)

//        binding.scrollResetBtn.visibility = View.INVISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_frame, HomeFragment())
//            .addToBackStack("homePage")
            .commit()

        init()
    }

    private fun init() {
        loadingDialog = com.devJoa.LoadingDialog(this)

        val color = ContextCompat.getColor(this, R.color.sky_blue_color)
        binding.homeFab.backgroundTintList = ColorStateList.valueOf(color)

        binding.drawerNav.nicknameTv.text = user.nickname + " 님"

        val url = ApplicationClass.SERVER_URL + "${user.imageUrl}"
        Log.d(TAG, "init: ${user}")
        Glide.with(this)
            .load(Uri.parse(url)).skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .apply {
                into(binding.profileImage)
                into(binding.drawerNav.drawerProfileImage)
            }

        binding.drawerNav.myPageTv.setOnClickListener {
            pageViewModel.setUser(ApplicationClass.sharedPreferencesUtil.getUser())
            changeFragment("myPage")
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.homeFab.setOnClickListener {
            changeFragment("homePage")
        }
        binding.postFab.setOnClickListener {
            changeFragment("postPage")
        }
        binding.searchUserFab.setOnClickListener {
            changeFragment("searchUserPage")
        }
        binding.logoImage.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.profileImage.setOnClickListener {
            pageViewModel.setUser(ApplicationClass.sharedPreferencesUtil.getUser())
            changeFragment("myPage")
        }
        binding.drawerNav.logoutTv.setOnClickListener {
            logout()
        }
        binding.searchFab.setOnClickListener {
            changeFragment("searchPage")
        }
        binding.drawerNav.drawerSearchNickname.setOnClickListener {
            changeFragment("searchUserPage")
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.drawerNav.drawerHome.setOnClickListener {
            changeFragment("homePage")
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.drawerNav.drawerSearchPost.setOnClickListener {
            changeFragment("searchPage")
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        binding.appbarText.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    private fun getUserData(): User {
        val user = intent.getSerializableExtra("user") as User
        binding.drawerNav.nicknameTv.text = user.nickname + " 님"

        return user
    }

    private fun logout() {
        //preference 지우기
        ApplicationClass.sharedPreferencesUtil.deleteUser()

        //화면이동
        val intent = Intent(this, com.devJoa.MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    fun changeFragment(str: String) {
        when (str) {
            "homePage" -> {
                supportFragmentManager.popBackStack(
                    "homePage",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, HomeFragment())
                    .addToBackStack("homePage")
//                    .commit()
                    .commitAllowingStateLoss()
            }
            "myPage" -> {
                supportFragmentManager.popBackStack(
                    "myPage",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, MyPageFragment())
                    .addToBackStack("myPage")
//                    .commit()
                    .commitAllowingStateLoss()
            }
            "productPage" -> {
                supportFragmentManager.popBackStack(
                    "productPage",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, ProductFragment())
                    .addToBackStack("productPage")
//                    .commit()
                    .commitAllowingStateLoss()
            }
            "postPage" -> {
                supportFragmentManager.popBackStack(
                    "postPage",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, PostFragment())
                    .addToBackStack("postPage")
//                    .commit()
                    .commitAllowingStateLoss()
            }
            "searchPage" -> {
                supportFragmentManager.popBackStack(
                    "searchPage",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, SearchPostFragment())
                    .addToBackStack("searchPage")
//                    .commit()
                    .commitAllowingStateLoss()
            }
            "postDetail" -> {
                supportFragmentManager.popBackStack(
                    "postDetail",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, PostDetailFragment())
                    .addToBackStack("postDetail")
//                    .commit()
                    .commitAllowingStateLoss()
            }
            "searchUserPage" -> {
                supportFragmentManager.popBackStack(
                    "searchUserPage",
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, SearchUserFragment())
                    .addToBackStack("searchUserPage")
//                    .commit()
                    .commitAllowingStateLoss()
            }
        }
    }

    fun nowPageFABToggle() {
        val fragmentManager = supportFragmentManager
        val backStackEntryCount = fragmentManager.backStackEntryCount
        Log.d(TAG, "백스택에 있는 프래그먼트 개수: ${backStackEntryCount}")
        if (backStackEntryCount > 0) {
            val topFragmentIndex = backStackEntryCount - 1
            val topFragmentEntry = fragmentManager.getBackStackEntryAt(topFragmentIndex)
            val topFragmentTag = topFragmentEntry.name
            Log.d(TAG, "nowPageFABToggle: ${topFragmentTag}")
//            val topFragment = fragmentManager.findFragmentByTag(topFragmentTag)

            topFragmentTag?.let { changeFABsColor(it) }
        }
    }

    fun changeFABsColor(page: String) {
        val colorBlue = ContextCompat.getColor(this, R.color.sky_blue_color)
        val colorButter = ContextCompat.getColor(this, R.color.butter_color)
        val colorWhite = ContextCompat.getColor(this, R.color.white)
        when (page) {
            "homePage" -> {
                binding.searchUserFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.homeFab.backgroundTintList = ColorStateList.valueOf(colorBlue)
                binding.postFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.searchFab.backgroundTintList = ColorStateList.valueOf(colorWhite)

                binding.scrollResetBtn.visibility = View.INVISIBLE
                binding.homeFab.visibility = View.VISIBLE
                binding.searchFab.visibility = View.VISIBLE
                binding.postFab.visibility = View.VISIBLE
                binding.searchUserFab.visibility = View.VISIBLE

                binding.homeFab.isClickable = false
                binding.postFab.isClickable = true
                binding.searchFab.isClickable = true
                binding.searchUserFab.isClickable = true

                binding.logoImageC.visibility = View.VISIBLE
                binding.appbarText.visibility = View.INVISIBLE
                binding.appBarLayout.visibility = View.VISIBLE
            }
            "postPage" -> {
                binding.searchUserFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.homeFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.postFab.backgroundTintList = ColorStateList.valueOf(colorBlue)
                binding.searchFab.backgroundTintList = ColorStateList.valueOf(colorWhite)

                binding.scrollResetBtn.visibility = View.VISIBLE
                binding.homeFab.visibility = View.VISIBLE
                binding.searchFab.visibility = View.VISIBLE
                binding.postFab.visibility = View.VISIBLE
                binding.searchUserFab.visibility = View.VISIBLE

                binding.homeFab.isClickable = true
                binding.postFab.isClickable = false
                binding.searchFab.isClickable = true
                binding.searchUserFab.isClickable = true

                binding.logoImageC.visibility = View.INVISIBLE
                binding.appbarText.visibility = View.VISIBLE
                binding.appbarText.text = "아이템"
                binding.appBarLayout.visibility = View.GONE
            }
            "searchPage" -> {
                binding.searchUserFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.homeFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.postFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.searchFab.backgroundTintList = ColorStateList.valueOf(colorBlue)

                binding.scrollResetBtn.visibility = View.INVISIBLE
                binding.homeFab.visibility = View.VISIBLE
                binding.searchFab.visibility = View.VISIBLE
                binding.postFab.visibility = View.VISIBLE
                binding.searchUserFab.visibility = View.VISIBLE

                binding.homeFab.isClickable = true
                binding.postFab.isClickable = true
                binding.searchFab.isClickable = false
                binding.searchUserFab.isClickable = true

                binding.logoImageC.visibility = View.INVISIBLE
                binding.appbarText.visibility = View.VISIBLE
                binding.appbarText.text = "아이템 검색"
                binding.appBarLayout.visibility = View.GONE
            }
            "postDetail" -> {
                binding.scrollResetBtn.visibility = View.INVISIBLE
                binding.homeFab.visibility = View.GONE
                binding.searchFab.visibility = View.GONE
                binding.postFab.visibility = View.GONE
                binding.searchUserFab.visibility = View.GONE

                binding.logoImageC.visibility = View.INVISIBLE
                binding.appbarText.visibility = View.VISIBLE
                binding.appbarText.text = "아이템 정보"
                binding.appBarLayout.visibility = View.GONE
            }
            "searchUserPage" -> {
                binding.searchUserFab.backgroundTintList = ColorStateList.valueOf(colorBlue)
                binding.homeFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.postFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
                binding.searchFab.backgroundTintList = ColorStateList.valueOf(colorWhite)

                binding.scrollResetBtn.visibility = View.INVISIBLE
                binding.homeFab.visibility = View.VISIBLE
                binding.searchFab.visibility = View.VISIBLE
                binding.postFab.visibility = View.VISIBLE
                binding.searchUserFab.visibility = View.VISIBLE

                binding.homeFab.isClickable = true
                binding.postFab.isClickable = true
                binding.searchFab.isClickable = true
                binding.searchUserFab.isClickable = false

                binding.logoImageC.visibility = View.INVISIBLE
                binding.appbarText.visibility = View.VISIBLE
                binding.appbarText.text = "유저 검색"
                binding.appBarLayout.visibility = View.GONE
            }
            else -> {
//                binding.homeFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
//                binding.postFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
//                binding.searchFab.backgroundTintList = ColorStateList.valueOf(colorWhite)
//                binding.searchUserFab.backgroundTintList = ColorStateList.valueOf((colorWhite))

                binding.scrollResetBtn.visibility = View.INVISIBLE
                binding.homeFab.visibility = View.INVISIBLE
                binding.searchFab.visibility = View.INVISIBLE
                binding.postFab.visibility = View.INVISIBLE
                binding.searchUserFab.visibility = View.INVISIBLE

                binding.homeFab.isClickable = true
                binding.postFab.isClickable = true
                binding.searchFab.isClickable = true

                binding.logoImageC.visibility = View.INVISIBLE
                binding.appbarText.visibility = View.VISIBLE
                binding.appbarText.text = "유저 정보"

                binding.appBarLayout.visibility = View.GONE
            }
        }
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onModifyClicked(user: User) {
        pageViewModel.setUser(user)
        CoroutineScope(Dispatchers.IO).launch {
            RetrofitUtil.userService.modifyUser(user)
        }
        ApplicationClass.sharedPreferencesUtil.deleteUser()
        ApplicationClass.sharedPreferencesUtil.addUser(user)
        binding.drawerNav.nicknameTv.text = user.nickname
    }

    override fun onModifyProfileClicked() {

    }


}